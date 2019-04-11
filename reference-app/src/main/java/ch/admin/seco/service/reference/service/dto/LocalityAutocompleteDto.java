package ch.admin.seco.service.reference.service.dto;

import java.util.List;

public class LocalityAutocompleteDto {

    private final List<LocalitySuggestionDto> localities;
    private final List<CantonSuggestionDto> cantons;

    public LocalityAutocompleteDto(List<LocalitySuggestionDto> localities, List<CantonSuggestionDto> cantons) {
        this.localities = localities;
        this.cantons = cantons;
    }

    public List<LocalitySuggestionDto> getLocalities() {
        return localities;
    }

    public List<CantonSuggestionDto> getCantons() {
        return cantons;
    }
}
