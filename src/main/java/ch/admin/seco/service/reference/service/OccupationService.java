package ch.admin.seco.service.reference.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.OccupationMapping;
import ch.admin.seco.service.reference.domain.OccupationSynonym;
import ch.admin.seco.service.reference.service.dto.OccupationAutocompleteDto;

/**
 * Service Interface for managing Occupations.
 */
public interface OccupationService {

    /**
     * Save a occupationSynonym.
     *
     * @param occupationSynonym the entity to save
     * @return the persisted entity
     */
    OccupationSynonym save(OccupationSynonym occupationSynonym);

    /**
     * Get all the occupationSynonyms.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<OccupationSynonym> findAll(Pageable pageable);

    /**
     * Get the "id" occupationSynonym.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<OccupationSynonym> findOne(UUID id);

    /**
     * Delete the "id" occupationSynonym.
     *
     * @param id the id of the entity
     */
    void delete(UUID id);

    /**
     * Search for the occupationSynonym corresponding to the query.
     *
     * @param prefix     the query of the search
     * @param language   the language of the search
     * @param resultSize the pagination information
     * @return the lists of occupations and classifications
     */
    OccupationAutocompleteDto suggestOccupationSynonyms(String prefix, Language language, int resultSize);

    /**
     * Get one occupationMapping by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<OccupationMapping> findOneOccupationMapping(UUID id);

    /**
     * Get all the occupationMappings.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<OccupationMapping> findAllOccupationMappings(Pageable pageable);

    Optional<Occupation> findOneOccupationByCode(int code);

    Optional<Occupation> findOneOccupationByAvamCode(int avamCode);

    Optional<Occupation> findOneOccupationByX28Code(int x28Code);

    Optional<OccupationSynonym> findOneOccupationSynonymByExternalId(int externalId);
}
