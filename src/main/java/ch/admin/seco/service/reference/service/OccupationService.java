package ch.admin.seco.service.reference.service;

import ch.admin.seco.service.reference.domain.Occupation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Service Interface for managing Occupation.
 */
public interface OccupationService {

    /**
     * Save a occupation.
     *
     * @param occupation the entity to save
     * @return the persisted entity
     */
    Occupation save(Occupation occupation);

    /**
     *  Get all the occupations.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Occupation> findAll(Pageable pageable);

    /**
     *  Get the "id" occupation.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Optional<Occupation> findOne(UUID id);

    /**
     *  Delete the "id" occupation.
     *
     *  @param id the id of the entity
     */
    void delete(UUID id);

    /**
     * Search for the occupation corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Occupation> search(String query, Pageable pageable);
}
