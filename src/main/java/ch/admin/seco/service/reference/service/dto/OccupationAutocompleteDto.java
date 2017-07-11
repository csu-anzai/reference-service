package ch.admin.seco.service.reference.service.dto;

import java.util.List;

public class OccupationAutocompleteDto {

    private final List<OccupationSuggestionDto> occupations;
    private final List<ClassificationSuggestionDto> classifications;

    public OccupationAutocompleteDto(List<OccupationSuggestionDto> occupations, List<ClassificationSuggestionDto> classifications) {
        this.occupations = occupations;
        this.classifications = classifications;
    }

    public List<OccupationSuggestionDto> getOccupations() {
        return occupations;
    }

    public List<ClassificationSuggestionDto> getClassifications() {
        return classifications;
    }
}
