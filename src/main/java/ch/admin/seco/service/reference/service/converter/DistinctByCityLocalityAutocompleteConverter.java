package ch.admin.seco.service.reference.service.converter;

import java.util.Map;

import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;

public class DistinctByCityLocalityAutocompleteConverter extends DefaultLocalityAutocompleteConverter {

    @Override
    protected String getMapKey(LocalitySuggestionDto dto) {
        return dto.getCity();
    }

    @Override
    protected LocalitySuggestionDto toLocalitySuggestionDto(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new LocalitySuggestionDto()
            .city(String.class.cast(source.get("city")))
            .communalCode(Integer.class.cast(source.get("communalCode")))
            .cantonCode(String.class.cast(source.get("cantonCode")))
            .regionCode(String.class.cast(source.get("regionCode")));
    }
}
