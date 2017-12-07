package ch.admin.seco.service.reference.service.converter;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import ch.admin.seco.service.reference.service.dto.CantonSuggestionDto;
import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;


public abstract class LocalitySuggestionConverter {

    public LocalityAutocompleteDto convert(SearchResponse searchResponse, int resultSize) {
        List<LocalitySuggestionDto> localities = convertLocalitiesSuggestions(searchResponse, resultSize);
        List<CantonSuggestionDto> cantons = convertCantonsSuggestions(searchResponse, resultSize);

        return new LocalityAutocompleteDto(localities, cantons);
    }

    protected abstract List<LocalitySuggestionDto> convertLocalitiesSuggestions(SearchResponse searchResponse, int resultSize);

    protected List<CantonSuggestionDto> convertCantonsSuggestions(SearchResponse searchResponse, int resultSize) {
        return convertSuggestionToDto(resultSize, searchResponse, this::toCantonSuggestionDto,
            "cantonCodes", "cantonNames");
    }

    protected <T> List<T> convertSuggestionToDto(int resultSize, SearchResponse searchResponse,
        Function<CompletionSuggestion.Entry.Option, T> mapperFunction, String... suggestionNames) {
        return Stream.of(suggestionNames)
            .flatMap(suggestionName -> searchResponse.getSuggest().<CompletionSuggestion>getSuggestion(suggestionName).getEntries().stream())
            .flatMap(item -> item.getOptions().stream())
            .map(mapperFunction)
            .distinct() // eliminate duplicates as 'Zürich'
            .limit(resultSize) // reduce the result list to the desired result size
            .collect(toList());
    }

    protected LocalitySuggestionDto toLocalitySuggestionDto(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new LocalitySuggestionDto()
            .city(String.class.cast(source.get("city")))
            .communalCode(Integer.class.cast(source.get("communalCode")))
            .cantonCode(String.class.cast(source.get("cantonCode")))
            .regionCode(String.class.cast(source.get("regionCode")))
            .zipCode(String.class.cast(source.get("zipCode")));
    }

    protected CantonSuggestionDto toCantonSuggestionDto(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new CantonSuggestionDto()
            .code(String.class.cast(source.get("code")))
            .name(String.class.cast(source.get("name")));
    }
}
