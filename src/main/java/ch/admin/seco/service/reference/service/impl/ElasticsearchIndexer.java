package ch.admin.seco.service.reference.service.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

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

import ch.admin.seco.service.reference.domain.Canton;
import ch.admin.seco.service.reference.domain.search.CantonSuggestion;
import ch.admin.seco.service.reference.domain.search.LocalitySuggestion;
import ch.admin.seco.service.reference.repository.CantonRepository;
import ch.admin.seco.service.reference.repository.LocalityRepository;
import ch.admin.seco.service.reference.repository.search.CantonSearchRepository;
import ch.admin.seco.service.reference.repository.search.LocalitySearchRepository;

@Component
class ElasticsearchIndexer {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexer.class);
    private final EntityManager entityManager;
    private final LocalityRepository localityRepository;
    private final LocalitySearchRepository localitySearchRepository;
    private final CantonRepository cantonRepository;
    private final CantonSearchRepository cantonSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EntityToSuggestionMapper entityToSynonymMapper;

    ElasticsearchIndexer(EntityManager entityManager,
            LocalityRepository localityRepository,
            LocalitySearchRepository localitySynonymSearchRepository,
            CantonRepository cantonRepository,
            CantonSearchRepository cantonSearchRepository,
            ElasticsearchTemplate elasticsearchTemplate,
            EntityToSuggestionMapper entityToSynonymMapper) {

        this.entityManager = entityManager;
        this.localityRepository = localityRepository;
        this.localitySearchRepository = localitySynonymSearchRepository;
        this.cantonRepository = cantonRepository;
        this.cantonSearchRepository = cantonSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.entityToSynonymMapper = entityToSynonymMapper;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void reindexLocalities() {
        disableHibernateSecondaryCache();
        elasticsearchTemplate.deleteIndex(LocalitySuggestion.class);
        elasticsearchTemplate.deleteIndex(CantonSuggestion.class);

        reindexLocality();
        reindexCanton();
    }

    private void reindexLocality() {
        elasticsearchTemplate.createIndex(LocalitySuggestion.class);
        elasticsearchTemplate.putMapping(LocalitySuggestion.class);

        reindexWithStream(localityRepository, localitySearchRepository, entityToSynonymMapper::toLocalitySuggestion, LocalitySuggestion.class);
    }

    private void reindexCanton() {
        elasticsearchTemplate.createIndex(CantonSuggestion.class);
        elasticsearchTemplate.putMapping(CantonSuggestion.class);

        reindexWithStream(cantonRepository, cantonSearchRepository, entityToSynonymMapper::toCantonSuggestion, Canton.class);
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
}
