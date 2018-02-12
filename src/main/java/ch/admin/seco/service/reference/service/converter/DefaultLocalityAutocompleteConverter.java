package ch.admin.seco.service.reference.service.converter;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import ch.admin.seco.service.reference.service.dto.CantonSuggestionDto;
import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;

public class DefaultLocalityAutocompleteConverter implements LocalityAutocompleteConverter {

    private final Supplier<Map<String, LocalitySuggestionDto>> mapSupplier = LinkedHashMap::new;
    private final BiConsumer<Map<String, LocalitySuggestionDto>, LocalitySuggestionDto> accumulator =
        (map, dto) -> map.put(getMapKey(dto), dto);
    private final BiConsumer<Map<String, LocalitySuggestionDto>, Map<String, LocalitySuggestionDto>> mapCombiner =
        (destination, source) -> source.forEach(destination::put);

    @Override
    public LocalityAutocompleteDto convert(SearchResponse searchResponse, int resultSize) {
        List<LocalitySuggestionDto> localities = convertLocalitiesSuggestions(searchResponse, resultSize);
        List<CantonSuggestionDto> cantons = convertCantonsSuggestions(searchResponse, resultSize);

        return new LocalityAutocompleteDto(localities, cantons);
    }

    protected String getMapKey(LocalitySuggestionDto dto) {
        return dto.getZipCode() + dto.getCity();
    }

    protected List<LocalitySuggestionDto> convertLocalitiesSuggestions(SearchResponse searchResponse, int resultSize) {
        return getSuggestOptionsStream(searchResponse, "cities", "zipCodes")
            .map(this::toLocalitySuggestionDto)
            .collect(mapSupplier, accumulator, mapCombiner)
            .values().stream()
            .limit(resultSize)
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

    protected List<CantonSuggestionDto> convertCantonsSuggestions(SearchResponse searchResponse, int resultSize) {
        return getSuggestOptionsStream(searchResponse, "cantonCodes", "cantonNames")
            .map(this::toCantonSuggestionDto)
            .distinct() // eliminate duplicates as 'Zürich'
            .sorted(Comparator.comparing(CantonSuggestionDto::getName))
            .limit(resultSize) // reduce the result list to the desired result size
            .collect(toList());
    }

    protected CantonSuggestionDto toCantonSuggestionDto(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new CantonSuggestionDto()
            .code(String.class.cast(source.get("code")))
            .name(String.class.cast(source.get("name")));
    }

    protected Stream<CompletionSuggestion.Entry.Option> getSuggestOptionsStream(SearchResponse searchResponse, String... suggestionNames) {
        if (isNull(searchResponse.getSuggest())) {
            return Stream.empty();
        }
        return Stream.of(suggestionNames)
            .flatMap(suggestionName -> searchResponse.getSuggest()
                .<CompletionSuggestion>getSuggestion(suggestionName).getEntries().stream())
            .flatMap(item -> item.getOptions().stream());
    }
}
