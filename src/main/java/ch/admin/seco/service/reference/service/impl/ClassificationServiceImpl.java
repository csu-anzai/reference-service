package ch.admin.seco.service.reference.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.Classification;
import ch.admin.seco.service.reference.domain.search.ClassificationSuggestion;
import ch.admin.seco.service.reference.repository.ClassificationRepository;
import ch.admin.seco.service.reference.repository.search.ClassificationSearchRepository;
import ch.admin.seco.service.reference.service.ClassificationService;

/**
 * Service Implementation for managing Classification.
 */
@Service
@Transactional
public class ClassificationServiceImpl implements ClassificationService {

    private final Logger log = LoggerFactory.getLogger(ClassificationServiceImpl.class);

    private final ApplicationContext applicationContext;
    private final ClassificationRepository classificationRepository;
    private final ClassificationSearchRepository classificationSearchRepository;
    private final EntityToSynonymMapper classificationMapper;
    private ClassificationServiceImpl classificationServiceImpl;

    public ClassificationServiceImpl(ApplicationContext applicationContext,
        ClassificationRepository classificationRepository,
        ClassificationSearchRepository classificationSearchRepository,
        EntityToSynonymMapper classificationMapper) {

        this.applicationContext = applicationContext;
        this.classificationRepository = classificationRepository;
        this.classificationSearchRepository = classificationSearchRepository;
        this.classificationMapper = classificationMapper;
    }

    /**
     * Save a classification.
     *
     * @param classification the entity to save
     * @return the persisted entity
     */
    @Override
    public Classification save(Classification classification) {
        log.debug("Request to save Classification : {}", classification);
        Classification result = classificationRepository.save(classification);
        classificationServiceImpl.index(result);
        return result;
    }

    /**
     *  Get all the classifications.
     *
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Classification> findAll(Pageable pageable) {
        log.debug("Request to get all Classifications");
        return classificationRepository.findAll(pageable);
    }

    /**
     *  Get one classification by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Classification> findOne(UUID id) {
        log.debug("Request to get Classification : {}", id);
        return classificationRepository.findById(id);
    }

    /**
     *  Delete the  classification by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Classification : {}", id);
        classificationRepository.findById(id).ifPresent(
            occupation -> {
                classificationRepository.delete(occupation);
                classificationSearchRepository.deleteAllByCodeEquals(occupation.getCode());
            }
        );
    }

    /**
     * Search for the classification corresponding to the query.
     *
     *  @param query the query of the suggest
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClassificationSuggestion> search(String query) {
        log.debug("Request to suggest Classifications for query {}", query);
        return StreamSupport
            .stream(classificationSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

    @Async
    void index(Classification classification) {
        classificationSearchRepository.save(classificationMapper.toClassificationSuggestion(classification));
    }

    @PostConstruct
    private void init() {
        classificationServiceImpl = applicationContext.getBean(ClassificationServiceImpl.class);
    }
}
