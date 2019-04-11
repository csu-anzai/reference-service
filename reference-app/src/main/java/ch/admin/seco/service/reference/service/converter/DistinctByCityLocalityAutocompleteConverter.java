package ch.admin.seco.service.reference.service.converter;

import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;

public class DistinctByCityLocalityAutocompleteConverter extends DefaultLocalityAutocompleteConverter {

    @Override
    protected String getMapKey(LocalitySuggestionDto dto) {
        return dto.getCity();
    }

    @Override
    protected LocalitySuggestionDto toLocalitySuggestionDto(CompletionSuggestion.Entry.Option option) {
        return toLocalitySuggestionDto(option.getHit().getSourceAsMap());
    }
}
