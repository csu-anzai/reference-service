package ch.admin.seco.service.reference.service.converter;

import java.util.List;

import org.elasticsearch.action.search.SearchResponse;

import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;

public class DefaultLocalitySuggestionConverter extends LocalitySuggestionConverter {

    @Override
    protected List<LocalitySuggestionDto> convertLocalitiesSuggestions(SearchResponse searchResponse, int resultSize) {
        return convertSuggestionToDto(resultSize, searchResponse, this::toLocalitySuggestionDto,
            "cities", "zipCodes");
    }
}
