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

import ch.admin.seco.service.reference.domain.search.OccupationSynonym;
import ch.admin.seco.service.reference.repository.OccupationRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSearchRepository;

@Service
public class ElasticsearchIndexServiceImpl implements ch.admin.seco.service.reference.service.ElasticsearchIndexService {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexServiceImpl.class);
    private final OccupationRepository occupationRepository;
    private final OccupationSearchRepository occupationSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final OccupationMapper occupationMapper;

    public ElasticsearchIndexServiceImpl(
        OccupationRepository occupationRepository,
        OccupationSearchRepository occupationSearchRepository,
        ElasticsearchTemplate elasticsearchTemplate,
        OccupationMapper occupationMapper) {
        this.occupationRepository = occupationRepository;
        this.occupationSearchRepository = occupationSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.occupationMapper = occupationMapper;
    }

    @Override
    @Async
    @Timed
    @Transactional(readOnly = true)
    public void reindexAll() {
        reindexOccupaton();
        log.info("Elasticsearch: Successfully performed reindexing");
    }

    private void reindexOccupaton() {
        Flux.fromStream(occupationRepository.streamAll())
            .map(occupation -> new OccupationSynonym()
                .id(occupation.getId())
                .code(occupation.getCode())
                .language(occupation.getLanguage())
                .occupation(occupation.getName())
                .occupationSuggestions(occupationMapper.extractSuggestionList(occupation.getName()))
            )
            .buffer(100)
            .subscribe(occupationSearchRepository::saveAll);
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
        log.info("Elasticsearch: Indexed {} of {} rows for {}", occupationSearchRepository.count(), occupationRepository.count(), entityClass.getSimpleName());
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
