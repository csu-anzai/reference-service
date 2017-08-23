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

import ch.admin.seco.service.reference.domain.search.ClassificationSynonym;
import ch.admin.seco.service.reference.domain.search.OccupationSynonym;
import ch.admin.seco.service.reference.repository.ClassificationRepository;
import ch.admin.seco.service.reference.repository.LocalityRepository;
import ch.admin.seco.service.reference.repository.OccupationSynonymRepository;
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
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EntityToSynonymMapper entityToSynonymMapper;

    public ElasticsearchIndexServiceImpl(
        ClassificationRepository classificationRepository,
        ClassificationSearchRepository classificationSearchRepository,
        OccupationSynonymRepository occupationSynonymRepository,
        OccupationSynonymSearchRepository occupationSynonymSearchRepository,
        LocalityRepository localityRepository,
        LocalitySynonymSearchRepository localitySynonymSearchRepository,
        ElasticsearchTemplate elasticsearchTemplate,
        EntityToSynonymMapper entityToSynonymMapper) {

        this.classificationRepository = classificationRepository;
        this.classificationSearchRepository = classificationSearchRepository;
        this.occupationSynonymRepository = occupationSynonymRepository;
        this.occupationSynonymSearchRepository = occupationSynonymSearchRepository;
        this.localityRepository = localityRepository;
        this.localitySynonymSearchRepository = localitySynonymSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.entityToSynonymMapper = entityToSynonymMapper;
    }

    @Override
    @Async
    @Timed
    @Transactional(readOnly = true)
    public void reindexAll() {
        reindexClassification();
        reindexOccupationSynonym();
        reindexLocality();
        log.info("Elasticsearch: Successfully performed reindexing");
    }

    private void reindexClassification() {
        Flux.fromStream(classificationRepository.streamAll())
            .map(classification -> new ClassificationSynonym()
                .id(classification.getId())
                .code(classification.getCode())
                .language(classification.getLanguage())
                .classification(classification.getName())
                .classificationSuggestions(entityToSynonymMapper.extractSuggestionList(classification.getName()))
            )
            .buffer(100)
            .subscribe(classificationSearchRepository::saveAll);
    }

    private void reindexOccupationSynonym() {
        Flux.fromStream(occupationSynonymRepository.streamAll())
            .map(occupationSynonym -> new OccupationSynonym()
                .id(occupationSynonym.getId())
                .code(occupationSynonym.getCode())
                .language(occupationSynonym.getLanguage())
                .occupation(occupationSynonym.getName())
                .occupationSuggestions(entityToSynonymMapper.extractSuggestionList(occupationSynonym.getName()))
            )
            .buffer(100)
            .subscribe(occupationSynonymSearchRepository::saveAll);
    }

    private void reindexLocality() {
        Flux.fromStream(localityRepository.streamAll())
            .map(entityToSynonymMapper::toSynonym)
            .buffer(100)
            .subscribe(localitySynonymSearchRepository::saveAll);
    }

    private <T, ID extends Serializable> void reindexForClass(Class<T> entityClass, JpaRepository<T, ID> jpaRepository,
        ElasticsearchRepository<T, ID> elasticsearchRepository) {
        elasticsearchTemplate.deleteIndex(entityClass);
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
