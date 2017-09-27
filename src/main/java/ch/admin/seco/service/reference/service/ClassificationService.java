package ch.admin.seco.service.reference.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ch.admin.seco.service.reference.domain.Classification;
import ch.admin.seco.service.reference.domain.search.ClassificationSuggestion;

/**
 * Service Interface for managing Classification.
 */
public interface ClassificationService {

    /**
     * Save a classification.
     *
     * @param classification the entity to save
     * @return the persisted entity
     */
    ch.admin.seco.service.reference.domain.Classification save(ch.admin.seco.service.reference.domain.Classification classification);

    /**
     *  Get all the classifications.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Classification> findAll(Pageable pageable);

    /**
     *  Get the "id" classification.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Optional<ch.admin.seco.service.reference.domain.Classification> findOne(UUID id);

    /**
     *  Delete the "id" classification.
     *
     *  @param id the id of the entity
     */
    void delete(UUID id);

    /**
     * Search for the classification corresponding to the query.
     *
     *  @param query the query of the suggest
     *
     *  @return the list of entities
     */
    List<ClassificationSuggestion> search(String query);
}
