package ch.admin.seco.service.reference.service.impl;

import static ch.admin.seco.service.reference.domain.enums.ProfessionCodeType.AVAM;
import static ch.admin.seco.service.reference.domain.enums.ProfessionCodeType.BFS;
import static ch.admin.seco.service.reference.domain.enums.ProfessionCodeType.SBN3;
import static ch.admin.seco.service.reference.domain.enums.ProfessionCodeType.SBN5;
import static ch.admin.seco.service.reference.domain.enums.ProfessionCodeType.X28;
import static java.util.Objects.isNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import ch.admin.seco.service.reference.domain.OccupationLabel;
import ch.admin.seco.service.reference.domain.OccupationLabelMappingRepository;
import ch.admin.seco.service.reference.domain.OccupationLabelRepository;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;
import ch.admin.seco.service.reference.domain.valueobject.OccupationLabelKey;
import ch.admin.seco.service.reference.service.search.OccupationLabelSearchRepository;
import ch.admin.seco.service.reference.service.search.OccupationLabelSuggestion;

@Component
public class ElasticsearchOccupationLabelIndexer {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchOccupationLabelIndexer.class);
    private final EntityManager entityManager;
    private final OccupationLabelRepository occupationLabelRepository;
    private final OccupationLabelMappingRepository occupationLabelMappingRepository;
    private final OccupationLabelSearchRepository occupationLabelSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final GenderNeutralOccupationLabelGenerator labelGenerator;

    ElasticsearchOccupationLabelIndexer(EntityManager entityManager,
        OccupationLabelRepository occupationLabelRepository,
        OccupationLabelMappingRepository occupationLabelMappingRepository,
        OccupationLabelSearchRepository occupationLabelSearchRepository,
        ElasticsearchTemplate elasticsearchTemplate,
        GenderNeutralOccupationLabelGenerator labelGenerator) {

        this.entityManager = entityManager;
        this.occupationLabelRepository = occupationLabelRepository;
        this.occupationLabelMappingRepository = occupationLabelMappingRepository;
        this.occupationLabelSearchRepository = occupationLabelSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.labelGenerator = labelGenerator;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void reindexOccupationLabel() {
        disableHibernateSecondaryCache();
        elasticsearchTemplate.deleteIndex(OccupationLabelSuggestion.class);
        elasticsearchTemplate.createIndex(OccupationLabelSuggestion.class);
        elasticsearchTemplate.putMapping(OccupationLabelSuggestion.class);

        reindexWithStream();
    }

    private Map<ProfessionCodeType, String> getOccupationLabelMapping(ProfessionCodeType professionType, String professionCode) {
        switch (professionType) {
            case AVAM:
                return occupationLabelMappingRepository.findOneByAvamCode(professionCode)
                    .map(mapping -> ImmutableMap.of(
                        AVAM, mapping.getAvamCode(),
                        BFS, mapping.getBfsCode(),
                        SBN3, mapping.getSbn3Code(),
                        SBN5, mapping.getSbn5Code()))
                    .orElse(null);

            case BFS:
                return occupationLabelMappingRepository.findByBfsCode(professionCode)
                    .stream()
                    .map(mapping -> ImmutableMap.of(
                        AVAM, mapping.getAvamCode(),
                        BFS, mapping.getBfsCode(),
                        SBN3, mapping.getSbn3Code(),
                        SBN5, mapping.getSbn5Code()))
                    .findFirst()
                    .orElse(null);

            case X28:
                return occupationLabelMappingRepository.findOneByX28Code(professionCode)
                    .map(mapping -> ImmutableMap.of(
                        X28, professionCode,
                        AVAM, mapping.getAvamCode(),
                        BFS, mapping.getBfsCode(),
                        SBN3, mapping.getSbn3Code(),
                        SBN5, mapping.getSbn5Code()))
                    .orElse(null);

            default:
                return ImmutableMap.of(professionType, professionCode);
        }
    }

    private void reindexWithStream() {
        try {
            long total = occupationLabelRepository.countAllKeys();
            AtomicInteger index = new AtomicInteger(0);
            AtomicInteger counter = new AtomicInteger(0);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Flux.fromStream(occupationLabelRepository.streamAllKeys())
                .flatMap(key -> {
                        counter.incrementAndGet();
                        if (X28.equals(key.getType())) {
                            return toX28OccupationLabelPublisher(key);
                        } else {
                            return toOccupationLabelPublisher(key);
                        }
                    }
                )
                .filter(occupationLabelSuggestion -> StringUtils.hasText(occupationLabelSuggestion.getLabel()))
                .buffer(1000)
                .doOnNext(occupationLabelSearchRepository::saveAll)
                .doOnNext(jobs ->
                    log.info("Index {} chunk #{}, {} / {}", OccupationLabel.class.getSimpleName(), index.incrementAndGet(), counter.get(), total))
                .doOnComplete(() -> {
                        stopWatch.stop();
                        log.info("Indexed {} suggestions of {} entities from {} in {} s", occupationLabelSearchRepository.count(), total, OccupationLabel.class.getSimpleName(), stopWatch.getTotalTimeSeconds());
                    }
                )
                .subscribe(jobs -> removeAllElementFromHibernatePrimaryCache());
        } catch (Exception ex) {
            log.error("Index of OccupationLabel table failed", ex);
        }
    }

    private Publisher<? extends OccupationLabelSuggestion> toOccupationLabelPublisher(OccupationLabelKey key) {
        List<OccupationLabel> occupationLabels = occupationLabelRepository.findByCodeAndTypeAndLanguage(key.getCode(), key.getType(), key.getLanguage());
        Map<String, String> labels = occupationLabels.stream().collect(Collectors.toMap(OccupationLabel::getClassifier, OccupationLabel::getLabel));
        String label = labels.get("default");
        if (!StringUtils.hasText(label)) {
            label = labelGenerator.generate(labels.get("m"), labels.get("f"));
            labels.put("default", label);
        }

        return Flux.just(new OccupationLabelSuggestion()
            .id(occupationLabels.size() == 1 ? occupationLabels.get(0).getId() : UUID.randomUUID())
            .code(key.getCode())
            .type(key.getType())
            .classifier("default")
            .language(key.getLanguage())
            .contextKey(String.format("%s:%s", key.getType(), key.getLanguage()))
            .mappings(getOccupationLabelMapping(key.getType(), key.getCode()))
            .occupationSuggestions(labels.values().stream().flatMap(this::extractSuggestions).filter(StringUtils::hasText).distinct().collect(Collectors.toSet()))
            .label(label));
    }

    private Publisher<? extends OccupationLabelSuggestion> toX28OccupationLabelPublisher(OccupationLabelKey key) {
        List<OccupationLabel> occupationLabels = occupationLabelRepository.findByCodeAndTypeAndLanguage(key.getCode(), key.getType(), key.getLanguage());
        return Flux.fromStream(occupationLabels.stream()
            .map(occupationLabel -> new OccupationLabelSuggestion()
                .id(occupationLabel.getId())
                .code(key.getCode())
                .type(key.getType())
                .classifier(occupationLabel.getClassifier())
                .language(key.getLanguage())
                .contextKey(String.format("%s:%s", key.getType(), key.getLanguage()))
                .mappings(getOccupationLabelMapping(key.getType(), key.getCode()))
                .occupationSuggestions(ImmutableSet.of(occupationLabel.getLabel()))
                .label(occupationLabel.getLabel())));
    }

    private void disableHibernateSecondaryCache() {
        ((Session) entityManager.getDelegate()).setCacheMode(CacheMode.IGNORE);
    }

    private void removeAllElementFromHibernatePrimaryCache() {
        entityManager.clear();
    }

    private Stream<String> extractSuggestions(String term) {
        if (isNull(term)) {
            return Stream.empty();
        }
        Set<String> suggestions = new HashSet<>();
        suggestions.add(term);
        Pattern pattern = Pattern.compile("[-_/\\\\. ]+");

        nextSubTerm(term, suggestions, pattern);
        return suggestions.stream();
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
