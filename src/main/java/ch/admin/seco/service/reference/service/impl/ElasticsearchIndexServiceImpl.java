package ch.admin.seco.service.reference.service.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import ch.admin.seco.service.reference.domain.Canton;
import ch.admin.seco.service.reference.domain.search.ClassificationSuggestion;
import ch.admin.seco.service.reference.domain.search.LocalitySuggestion;
import ch.admin.seco.service.reference.domain.search.OccupationSynonymSuggestion;
import ch.admin.seco.service.reference.repository.CantonRepository;
import ch.admin.seco.service.reference.repository.ClassificationRepository;
import ch.admin.seco.service.reference.repository.LocalityRepository;
import ch.admin.seco.service.reference.repository.OccupationSynonymRepository;
import ch.admin.seco.service.reference.repository.search.CantonSearchRepository;
import ch.admin.seco.service.reference.repository.search.ClassificationSearchRepository;
import ch.admin.seco.service.reference.repository.search.LocalitySynonymSearchRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSynonymSearchRepository;

@Service
public class ElasticsearchIndexServiceImpl implements ch.admin.seco.service.reference.service.ElasticsearchIndexService {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexServiceImpl.class);
    private final ClassificationRepository classificationRepository;
    private final ClassificationSearchRepository classificationSearchRepository;
    private final OccupationSynonymRepository occupationSynonymRepository;
    private final OccupationSynonymSearchRepository occupationSynonymSearchRepository;
    private final LocalityRepository localityRepository;
    private final LocalitySynonymSearchRepository localitySynonymSearchRepository;
    private final CantonRepository cantonRepository;
    private final CantonSearchRepository cantonSearchRepository;


    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EntityToSynonymMapper entityToSynonymMapper;

    public ElasticsearchIndexServiceImpl(
        ClassificationRepository classificationRepository, ClassificationSearchRepository classificationSearchRepository,
        OccupationSynonymRepository occupationSynonymRepository, OccupationSynonymSearchRepository occupationSynonymSearchRepository,
        LocalityRepository localityRepository, LocalitySynonymSearchRepository localitySynonymSearchRepository,
        CantonRepository cantonRepository, CantonSearchRepository cantonSearchRepository,
        ElasticsearchTemplate elasticsearchTemplate,
        EntityToSynonymMapper entityToSynonymMapper) {

        this.classificationRepository = classificationRepository;
        this.classificationSearchRepository = classificationSearchRepository;
        this.occupationSynonymRepository = occupationSynonymRepository;
        this.occupationSynonymSearchRepository = occupationSynonymSearchRepository;
        this.localityRepository = localityRepository;
        this.localitySynonymSearchRepository = localitySynonymSearchRepository;
        this.cantonRepository = cantonRepository;
        this.cantonSearchRepository = cantonSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.entityToSynonymMapper = entityToSynonymMapper;
    }

    @Override
    @Async
    @Timed
    @Transactional(readOnly = true)
    public void reindexAll() {
        reindexOccupationIndex();

        reindexLocalityIndex();

        log.info("Elasticsearch: Successfully performed reindexing");
    }

    private void reindexOccupationIndex() {
        elasticsearchTemplate.deleteIndex(ClassificationSuggestion.class);
        elasticsearchTemplate.deleteIndex(OccupationSynonymSuggestion.class);
        reindexClassification();
        reindexOccupationSynonym();
    }

    private void reindexClassification() {
        elasticsearchTemplate.createIndex(ClassificationSuggestion.class);
        elasticsearchTemplate.putMapping(ClassificationSuggestion.class);

        StopWatch watch = new StopWatch();
        watch.start();

        Flux.fromStream(classificationRepository.streamAll())
            .map(classification -> new ClassificationSuggestion()
                .id(classification.getId())
                .code(classification.getCode())
                .classificationSuggestions(entityToSynonymMapper.extractSuggestions(classification.getLabels()))
            )
            .buffer(100)
            .subscribe(classificationSearchRepository::saveAll);

        watch.stop();
        log.debug("Elasticsearch has indexed the {} index in {} ms", ClassificationSuggestion.class.getSimpleName(), watch.getTotalTimeMillis());

        log.info("Elasticsearch: Indexed {} of {} rows for {}", classificationSearchRepository.count(), classificationRepository.count(), ClassificationSuggestion.class.getSimpleName());

    }

    private void reindexOccupationSynonym() {
        elasticsearchTemplate.createIndex(OccupationSynonymSuggestion.class);
        elasticsearchTemplate.putMapping(OccupationSynonymSuggestion.class);

        StopWatch watch = new StopWatch();
        watch.start();

        Flux.fromStream(occupationSynonymRepository.streamAll())
            .map(occupationSynonym -> new OccupationSynonymSuggestion()
                .id(occupationSynonym.getId())
                .code(occupationSynonym.getCode())
                .language(occupationSynonym.getLanguage())
                .name(occupationSynonym.getName())
                .occupationSuggestions(entityToSynonymMapper.extractSuggestionList(occupationSynonym.getName()))
            )
            .buffer(100)
            .subscribe(occupationSynonymSearchRepository::saveAll);

        watch.stop();
        log.debug("Elasticsearch has indexed the {} index in {} ms", OccupationSynonymSuggestion.class.getSimpleName(), watch.getTotalTimeMillis());

        log.info("Elasticsearch: Indexed {} of {} rows for {}", occupationSynonymSearchRepository.count(), occupationSynonymRepository.count(), OccupationSynonymSuggestion.class.getSimpleName());
    }

    private void reindexLocalityIndex() {
        elasticsearchTemplate.deleteIndex(LocalitySuggestion.class);
        elasticsearchTemplate.deleteIndex(Canton.class);

        StopWatch watch = new StopWatch();
        watch.start();

        reindexLocality();
        reindexForClass(Canton.class, cantonRepository, cantonSearchRepository);

        watch.stop();
        log.debug("Elasticsearch has indexed the Locality index in {} ms", watch.getTotalTimeMillis());
    }

    private void reindexLocality() {
        elasticsearchTemplate.createIndex(LocalitySuggestion.class);
        elasticsearchTemplate.putMapping(LocalitySuggestion.class);

        Flux.fromStream(localityRepository.streamAll())
            .map(entityToSynonymMapper::toSuggestion)
            .buffer(100)
            .subscribe(localitySynonymSearchRepository::saveAll);

        log.info("Elasticsearch: Indexed {} of {} rows for {}", localitySynonymSearchRepository.count(), localityRepository.count(), LocalitySuggestion.class.getSimpleName());
    }

    private <T, ID extends Serializable> void reindexForClass(Class<T> entityClass, JpaRepository<T, ID> jpaRepository,
        ElasticsearchRepository<T, ID> elasticsearchRepository) {
        elasticsearchTemplate.createIndex(entityClass);
        elasticsearchTemplate.putMapping(entityClass);

        if (jpaRepository.count() > 0) {
            try {
                reindexWithStream(jpaRepository, elasticsearchRepository);
            } catch (Exception e) {
                reindexWithPageable(jpaRepository, elasticsearchRepository);
            }
        }
        log.info("Elasticsearch: Indexed {} of {} rows for {}", elasticsearchRepository.count(), jpaRepository.count(), entityClass.getSimpleName());
    }

    private <T, ID extends Serializable> void reindexWithStream(JpaRepository<T, ID> jpaRepository, ElasticsearchRepository<T, ID> elasticsearchRepository) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method m = jpaRepository.getClass().getMethod("streamAll");
        Stream<T> stream = Stream.class.cast(m.invoke(jpaRepository));
        Flux.fromStream(stream)
            .buffer(100)
            .subscribe(elasticsearchRepository::saveAll);
    }

    private <T, ID extends Serializable> void reindexWithPageable(JpaRepository<T, ID> jpaRepository, ElasticsearchRepository<T, ID> elasticsearchRepository) {
        Pageable pageable = PageRequest.of(0, 100);
        Page<T> entities = null;
        do {
            entities = jpaRepository.findAll(pageable);
            elasticsearchRepository.saveAll(entities.getContent());
            pageable = pageable.next();
        } while (entities.hasNext());
    }
}
