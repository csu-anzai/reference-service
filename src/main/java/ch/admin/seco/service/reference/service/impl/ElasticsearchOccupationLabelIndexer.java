package ch.admin.seco.service.reference.service.impl;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import com.google.common.collect.ImmutableMap;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import ch.admin.seco.service.reference.domain.OccupationLabel;
import ch.admin.seco.service.reference.domain.search.OccupationLabelSuggestion;
import ch.admin.seco.service.reference.repository.OccupationLabelMappingRepository;
import ch.admin.seco.service.reference.repository.OccupationLabelRepository;
import ch.admin.seco.service.reference.repository.search.OccupationLabelSearchRepository;

@Component
class ElasticsearchOccupationLabelIndexer {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchOccupationLabelIndexer.class);
    private final EntityManager entityManager;
    private final OccupationLabelRepository occupationLabelRepository;
    private final OccupationLabelMappingRepository occupationMappingRepository;
    private final OccupationLabelSearchRepository occupationSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;

    ElasticsearchOccupationLabelIndexer(EntityManager entityManager,
        OccupationLabelRepository occupationLabelRepository,
        OccupationLabelMappingRepository occupationMappingRepository,
        OccupationLabelSearchRepository occupationRepository,
        ElasticsearchTemplate elasticsearchTemplate) {

        this.entityManager = entityManager;
        this.occupationLabelRepository = occupationLabelRepository;
        this.occupationMappingRepository = occupationMappingRepository;
        this.occupationSearchRepository = occupationRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void reindexOccupationIndex() {
        disableHibernateSecondaryCache();
        elasticsearchTemplate.deleteIndex(OccupationLabelSuggestion.class);
        reindexOccupation();
    }

    public void indexOccupationLabel(OccupationLabel occupationLabel) {
        OccupationLabelSuggestion suggestion = toOccupationSuggestion(occupationLabel);
        this.occupationSearchRepository.save(suggestion);
    }

    OccupationLabelSuggestion toOccupationSuggestion(OccupationLabel occupationLabel) {
        return new OccupationLabelSuggestion()
            .id(occupationLabel.getId())
            .type(occupationLabel.getType())
            .code(occupationLabel.getCode())
            .classifier(occupationLabel.getClassifier())
            .language(occupationLabel.getLanguage())
            .label(occupationLabel.getLabel())
            .contextKey(String.format("%s:%s", occupationLabel.getType(), occupationLabel.getLanguage()))
            .occupationSuggestions(extractSuggestions(occupationLabel.getLabel()))
            .mappings(getOccupationMapping(occupationLabel.getType(), occupationLabel.getCode()));
    }

    private void reindexOccupation() {
        elasticsearchTemplate.createIndex(OccupationLabelSuggestion.class);
        elasticsearchTemplate.putMapping(OccupationLabelSuggestion.class);

        reindexWithStream(occupationLabelRepository, occupationSearchRepository, this::toOccupationSuggestion, OccupationLabel.class);
    }

    private Map<String, Integer> getOccupationMapping(String type, int code) {
        switch (type) {
            case "avam":
                return occupationMappingRepository.findOneByAvamCode(code)
                    .map(mapping -> ImmutableMap.of(
                        "avam", mapping.getAvamCode(),
                        "bfs", mapping.getBfsCode(),
                        "sbn3", mapping.getSbn3Code(),
                        "sbn5", mapping.getSbn5Code()))
                    .orElse(null);

            case "bfs":
                return occupationMappingRepository.findByBfsCode(code)
                    .stream()
                    .map(mapping -> ImmutableMap.of(
                        "avam", mapping.getAvamCode(),
                        "bfs", mapping.getBfsCode(),
                        "sbn3", mapping.getSbn3Code(),
                        "sbn5", mapping.getSbn5Code()))
                    .findFirst()
                    .orElse(null);

            case "x28":
                return occupationMappingRepository.findOneByX28Code(code)
                    .map(mapping -> ImmutableMap.of(
                        "x28", code,
                        "avam", mapping.getAvamCode(),
                        "bfs", mapping.getBfsCode(),
                        "sbn3", mapping.getSbn3Code(),
                        "sbn5", mapping.getSbn5Code()))
                    .orElse(null);

            default:
                return ImmutableMap.of("type", code);
        }
    }

    private <JPA, ELASTIC, ID extends Serializable> void reindexWithStream(
        JpaRepository<JPA, ID> jpaRepository,
        ElasticsearchRepository<ELASTIC, ID> elasticsearchRepository,
        Function<JPA, ELASTIC> mapEntityToIndex, Class entityClass) {
        try {
            Method m = jpaRepository.getClass().getMethod("streamAll");
            long total = jpaRepository.count();
            AtomicInteger index = new AtomicInteger(0);
            AtomicInteger counter = new AtomicInteger(0);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Stream<JPA> stream = Stream.class.cast(m.invoke(jpaRepository));
            Flux.fromStream(stream)
                .map(mapEntityToIndex)
                .buffer(1000)
                .doOnNext(elasticsearchRepository::saveAll)
                .doOnNext(jobs ->
                    log.info("Index {} chunk #{}, {} / {}", entityClass.getSimpleName(), index.incrementAndGet(), counter.addAndGet(jobs.size()), total))
                .doOnComplete(() -> {
                        stopWatch.stop();
                        log.info("Indexed {} of {} entities from {} in {} s", elasticsearchRepository.count(), jpaRepository.count(), entityClass.getSimpleName(), stopWatch.getTotalTimeSeconds());
                    }
                )
                .subscribe(jobs -> removeAllElementFromHibernatePrimaryCache());
        } catch (Exception e) {
            log.error("ReindexWithStream failed", e);
        }
    }

    private void disableHibernateSecondaryCache() {
        ((Session) entityManager.getDelegate()).setCacheMode(CacheMode.IGNORE);
    }

    private void removeAllElementFromHibernatePrimaryCache() {
        entityManager.clear();
    }

    private Set<String> extractSuggestions(String term) {
        if (isNull(term)) {
            return Collections.emptySet();
        }
        Set<String> suggestions = new HashSet<>();
        suggestions.add(term);
        Pattern pattern = Pattern.compile("[-_/\\\\. ]+");

        nextSubTerm(term, suggestions, pattern);

        suggestions.remove("");
        suggestions.remove(null);
        return suggestions;
    }

    private void nextSubTerm(String term, Set<String> suggestions, Pattern pattern) {
        Matcher matcher = pattern.matcher(term);
        if (matcher.find()) {
            term = term.substring(matcher.end());
            suggestions.add(term);
            nextSubTerm(term, suggestions, pattern);
        }
    }
}
