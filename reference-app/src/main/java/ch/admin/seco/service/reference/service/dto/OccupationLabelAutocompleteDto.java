package ch.admin.seco.service.reference.service.dto;

import java.util.Collection;

import ch.admin.seco.service.reference.domain.OccupationLabel;

public class OccupationLabelAutocompleteDto {

    private final Collection<OccupationLabelSuggestionDto> occupations;
    private final Collection<OccupationLabel> classifications;

    public OccupationLabelAutocompleteDto(Collection<OccupationLabelSuggestionDto> occupations, Collection<OccupationLabel> classifications) {
        this.occupations = occupations;
        this.classifications = classifications;
    }

    public Collection<OccupationLabelSuggestionDto> getOccupations() {
        return occupations;
    }

    public Collection<OccupationLabel> getClassifications() {
        return classifications;
    }
}
