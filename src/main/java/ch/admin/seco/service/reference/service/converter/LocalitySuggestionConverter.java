package ch.admin.seco.service.reference.service.converter;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
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
        return convertSuggestionToDto(resultSize, searchResponse, this::toCantonSuggestionDto, CantonSuggestionDto::getName,
                "cantonCodes", "cantonNames");
    }

    protected <T> List<T> convertSuggestionToDto(int resultSize, SearchResponse searchResponse,
            Function<CompletionSuggestion.Entry.Option, T> mapperFunction, Function<T, String> sortFunction, String... suggestionNames) {
        return Stream.of(suggestionNames)
                .flatMap(suggestionName -> searchResponse.getSuggest().<CompletionSuggestion>getSuggestion(suggestionName).getEntries().stream())
                .flatMap(item -> item.getOptions().stream())
                .map(mapperFunction)
                .distinct() // eliminate duplicates as 'Zürich'
                .sorted(Comparator.comparing(sortFunction))
                .limit(resultSize) // reduce the result list to the desired result size
                .collect(toList());
    }

    protected CantonSuggestionDto toCantonSuggestionDto(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new CantonSuggestionDto()
                .code(String.class.cast(source.get("code")))
                .name(String.class.cast(source.get("name")));
    }
}
