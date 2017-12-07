package ch.admin.seco.service.reference.service.converter;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;

public class DistinctLocalitySuggestionConverter extends LocalitySuggestionConverter {

    private static final Supplier<Map<String, LocalitySuggestionDto>> mapSupplier = LinkedHashMap::new;
    private static final BiConsumer<Map<String, LocalitySuggestionDto>, LocalitySuggestionDto> accumulator =
        (map, dto) -> map.put(dto.getCity(), dto);
    private static final BiConsumer<Map<String, LocalitySuggestionDto>, Map<String, LocalitySuggestionDto>> mapCombiner =
        (destination, source) -> source.forEach(destination::put);

    @Override
    protected List<LocalitySuggestionDto> convertLocalitiesSuggestions(SearchResponse searchResponse, int resultSize) {
        List<LocalitySuggestionDto> cities = convertSuggestionToDto(resultSize,
            searchResponse, this::toLocalitySuggestionDto, "cities");
        List<LocalitySuggestionDto> zipCodes = convertSuggestionsByZipCode(searchResponse, resultSize);

        return Stream.of(cities, zipCodes)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private List<LocalitySuggestionDto> convertSuggestionsByZipCode(SearchResponse searchResponse, int resultSize) {

        return searchResponse.getSuggest()
            .<CompletionSuggestion>getSuggestion("zipCodes").getEntries().stream()
            .flatMap(option -> option.getOptions().stream())
            .map(this::toLocalitySuggestionDto)
            .collect(mapSupplier, accumulator, mapCombiner)
            .values().stream()
            .limit(resultSize)
            .collect(toList());
    }

}
