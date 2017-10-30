package ch.admin.seco.service.reference.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.OccupationMapping;
import ch.admin.seco.service.reference.domain.OccupationSynonym;
import ch.admin.seco.service.reference.service.dto.OccupationAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.OccupationDto;

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

    Optional<OccupationDto> findOneOccupationByCode(int code, Language language);

    Optional<OccupationDto> findOneOccupationByAvamCode(int avamCode, Language language);

    Optional<OccupationDto> findOneOccupationByX28Code(int x28Code, Language language);

    Optional<OccupationSynonym> findOneOccupationSynonymByExternalId(int externalId);

    List<OccupationSynonym> save(Collection<OccupationSynonym> occupationSynonyms);

    /**
     * Search for the occupationSynonym corresponding to the query.
     *
     * @param prefix     the query of the suggest
     * @param language   the language of the suggest
     * @param includeSynonyms   include synonym into the search
     * @param resultSize the pagination information
     * @return the lists of occupations and classifications
     */
    OccupationAutocompleteDto suggest(String prefix, Language language, boolean includeSynonyms, int resultSize);

    List<Occupation> saveOccupations(Collection<Occupation> occupations);
}
