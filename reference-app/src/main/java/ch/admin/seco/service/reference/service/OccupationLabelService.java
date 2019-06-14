package ch.admin.seco.service.reference.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import ch.admin.seco.service.reference.domain.OccupationLabel;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;
import ch.admin.seco.service.reference.service.dto.OccupationLabelAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.OccupationLabelDto;
import ch.admin.seco.service.reference.service.dto.OccupationLabelMappingDto;
import ch.admin.seco.service.reference.service.dto.OccupationLabelSearchRequestDto;
import ch.admin.seco.service.reference.service.dto.OccupationLabelSuggestionDto;
import ch.admin.seco.service.reference.service.dto.ProfessionCodeDTO;

/**
 * Service Interface for managing Occupations.
 */
public interface OccupationLabelService {

    OccupationLabel save(OccupationLabel occupationLabel);

    Optional<OccupationLabelMappingDto> findOneOccupationMapping(ProfessionCodeDTO professionCode);

    /**
     * Search for the occupationSynonym corresponding to the query.
     *
     * @param prefix     the query of the suggest
     * @param language   the language of the suggest
     * @param types      search only within these types
     * @param resultSize the pagination information
     * @return the lists of occupations and classifications
     */
    OccupationLabelAutocompleteDto suggest(String prefix, Language language, Collection<ProfessionCodeType> types, int resultSize);

    Map<String, String> getOccupationLabels(ProfessionCodeDTO professionCode, Language language);

    Optional<Map<String, String>> getOccupationLabels(ProfessionCodeDTO professionCode, Language language,
        String classifier);

    Page<OccupationLabel> search(OccupationLabelSearchRequestDto searchRequest, Language language);

    List<OccupationLabelDto> getOccupationLabelsByClassification(ProfessionCodeDTO professionCodeDTO, Language language);

    Optional<OccupationLabelSuggestionDto> getOccupationInfoById(UUID id);

    Optional<OccupationLabelSuggestionDto> findOneByCodeTypeLanguageClassifier(String code, ProfessionCodeType type, Language language, String classifier);
}
