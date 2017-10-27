package ch.admin.seco.service.reference.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.search.LocalitySuggestion;
import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;
import ch.admin.seco.service.reference.repository.search.LocalitySynonymSearchRepository;
import ch.admin.seco.service.reference.service.dto.CantonSuggestionDto;
import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;

/**
 * Service Implementation for managing Locality.
 */
@Service
@Transactional
public class LocalitySuggestionImpl {

    private final LocalitySynonymSearchRepository localitySynonymSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EntityToSynonymMapper entityToSynonymMapper;

    public LocalitySuggestionImpl(
        LocalitySynonymSearchRepository localitySynonymSearchRepository,
        ElasticsearchTemplate elasticsearchTemplate,
        EntityToSynonymMapper entityToSynonymMapper) {
        this.localitySynonymSearchRepository = localitySynonymSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.entityToSynonymMapper = entityToSynonymMapper;
    }

    /**
     * Search for the locality corresponding
     * to the prefix and limit result by resultSize.
     *
     * @param prefix the prefix of the locality suggest
     * @param resultSize the response size information
     * @return the result of the suggest
     */
    public LocalityAutocompleteDto suggest(String prefix, int resultSize) {
        CompletionSuggestionBuilder citySuggestions = new CompletionSuggestionBuilder("citySuggestions")
            .prefix(prefix)
            .size(Math.min(resultSize + 20, 10000)); // to factor in duplicate entries as 'Zürich' we increase the result size

        SuggestBuilder suggestBuilder = new SuggestBuilder()
            .addSuggestion("cities", citySuggestions)
            .addSuggestion("zipCodes", new CompletionSuggestionBuilder("zipCode").prefix(prefix))
            .addSuggestion("cantonCodes", new CompletionSuggestionBuilder("code.canton-suggestions").prefix(prefix))
            .addSuggestion("cantonNames", new CompletionSuggestionBuilder("cantonSuggestions").prefix(prefix));
        SearchResponse searchResponse = elasticsearchTemplate.suggest(suggestBuilder, LocalitySuggestion.class);

        List<LocalitySuggestionDto> localities = convertSuggestionToDto(resultSize, searchResponse,
            entityToSynonymMapper::convertLocalitySuggestion, "cities", "zipCodes");

        List<CantonSuggestionDto> cantons = convertSuggestionToDto(resultSize, searchResponse,
            entityToSynonymMapper::convertCantonSuggestion, "cantonCodes", "cantonNames");

        return new LocalityAutocompleteDto(localities, cantons);
    }

    /**
     * Search for the nearest locality to the geo point.
     *
     * @param geoPoint the geo point from which to suggest
     * @return nearest entity to geo point
     */
    public Optional<Locality> findNearestLocality(GeoPoint geoPoint) {
        GeoDistanceSortBuilder distanceSortBuilder = SortBuilders.geoDistanceSort("geoPoint", geoPoint.getLatitude(), geoPoint.getLongitude())
            .unit(DistanceUnit.KILOMETERS)
            .geoDistance(GeoDistance.PLANE);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
            .withSort(distanceSortBuilder)
            .withPageable(PageRequest.of(0, 1))
            .build();
        return localitySynonymSearchRepository.search(query)
            .getContent()
            .stream().findFirst()
            .map(entityToSynonymMapper::fromSynonym);
    }

    private <T> List<T> convertSuggestionToDto(int resultSize, SearchResponse searchResponse,
        Function<CompletionSuggestion.Entry.Option, T> mapperFunction, String... suggestionNames) {
        return Stream.of(suggestionNames)
            .flatMap(suggestionName -> searchResponse.getSuggest().<CompletionSuggestion>getSuggestion(suggestionName).getEntries().stream())
            .flatMap(item -> item.getOptions().stream())
            .map(mapperFunction)
            .distinct() // eliminate duplicates as 'Zürich'
            .limit(resultSize) // reduce the result list to the desired result size
            .collect(toList());
    }
}
