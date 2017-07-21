package ch.admin.seco.service.reference.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.admin.seco.service.reference.domain.Classification;
import ch.admin.seco.service.reference.domain.search.ClassificationSynonym;

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
    Classification save(Classification classification);

    /**
     *  Get all the classifications.
     *
     *  @return the list of entities
     */
    List<Classification> findAll();

    /**
     *  Get the "id" classification.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Optional<Classification> findOne(UUID id);

    /**
     *  Delete the "id" classification.
     *
     *  @param id the id of the entity
     */
    void delete(UUID id);

    /**
     * Search for the classification corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @return the list of entities
     */
    List<ClassificationSynonym> search(String query);
}
