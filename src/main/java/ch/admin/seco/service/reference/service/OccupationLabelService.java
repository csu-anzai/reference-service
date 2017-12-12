package ch.admin.seco.service.reference.service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.OccupationLabel;
import ch.admin.seco.service.reference.domain.OccupationLabelMapping;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.service.dto.OccupationLabelAutocompleteDto;

/**
 * Service Interface for managing Occupations.
 */
public interface OccupationLabelService {

    OccupationLabel save(OccupationLabel occupationLabel);

    @Deprecated
    @Transactional(readOnly = true)
    Optional<OccupationLabelMapping> findOneOccupationMappingByBfsCode(int bfsCode);

    @Deprecated
    @Transactional(readOnly = true)
    Optional<OccupationLabelMapping> findOneOccupationMappingByAvamCode(int avamCode);

    @Deprecated
    @Transactional(readOnly = true)
    Optional<OccupationLabelMapping> findOneOccupationMappingByX28Code(int x28Code);

    @Transactional(readOnly = true)
    Optional<OccupationLabelMapping> findOneOccupationMapping(String type, int code);

    /**
     * Search for the occupationSynonym corresponding to the query.
     *
     * @param prefix     the query of the suggest
     * @param language   the language of the suggest
     * @param types      search only within these types
     * @param resultSize the pagination information
     * @return the lists of occupations and classifications
     */
    OccupationLabelAutocompleteDto suggest(String prefix, Language language, Collection<String> types, int resultSize);

    @Transactional(readOnly = true)
    Map<String, String> getOccupationLabels(int code, String type, Language language);

    @Transactional(readOnly = true)
    Optional<Map<String, String>> getOccupationLabels(int code, String type, Language language, String classifier);
}
