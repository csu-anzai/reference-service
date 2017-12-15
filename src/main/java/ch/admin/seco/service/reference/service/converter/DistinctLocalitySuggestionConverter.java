package ch.admin.seco.service.reference.service.converter;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;

public class DistinctLocalitySuggestionConverter extends DefaultLocalitySuggestionConverter {

    @Override
    protected List<LocalitySuggestionDto> convertLocalitiesSuggestions(SearchResponse searchResponse, int resultSize) {
        return convertSuggestionToDto(resultSize, searchResponse, this::toLocalitySuggestionDto, LocalitySuggestionDto::getCity,
                "cities", "zipCodes");
    }

    protected LocalitySuggestionDto toLocalitySuggestionDto(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new LocalitySuggestionDto()
                .city(String.class.cast(source.get("city")))
                .communalCode(Integer.class.cast(source.get("communalCode")))
                .cantonCode(String.class.cast(source.get("cantonCode")))
                .regionCode(String.class.cast(source.get("regionCode")));
    }
}
