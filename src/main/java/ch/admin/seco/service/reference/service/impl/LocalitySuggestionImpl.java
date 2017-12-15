package ch.admin.seco.service.reference.service.impl;

import java.util.Optional;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.suggest.SuggestBuilder;
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
import ch.admin.seco.service.reference.repository.search.LocalitySearchRepository;
import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.LocalitySearchDto;
import ch.admin.seco.service.reference.service.factory.LocalitySuggestionConverterFactory;

/**
 * Service Implementation for managing Locality.
 */
@Service
@Transactional
public class LocalitySuggestionImpl {

    private final LocalitySearchRepository localitySynonymSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EntityToSuggestionMapper entityToSynonymMapper;

    public LocalitySuggestionImpl(LocalitySearchRepository localitySynonymSearchRepository,
        ElasticsearchTemplate elasticsearchTemplate,
        EntityToSuggestionMapper entityToSynonymMapper) {

        this.localitySynonymSearchRepository = localitySynonymSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.entityToSynonymMapper = entityToSynonymMapper;
    }

    /**
     * Search for the locality corresponding
     * to the prefix and limit result by resultSize.
     *
     * @param localitySearchDTO suggest request parameters
     * @return the result of the suggest
     */
    public LocalityAutocompleteDto suggest(LocalitySearchDto localitySearchDTO) {
        SuggestBuilder suggestBuilder = createSuggestBuilder(localitySearchDTO);
        SearchResponse searchResponse = elasticsearchTemplate.suggest(suggestBuilder, LocalitySuggestion.class);
        return LocalitySuggestionConverterFactory.getConverter(localitySearchDTO)
            .convert(searchResponse, localitySearchDTO.getSize());
    }

    private SuggestBuilder createSuggestBuilder(LocalitySearchDto localitySearchDTO) {
        final String query = localitySearchDTO.getQuery();
        final int increasedResultSize = Math.min(localitySearchDTO.getSize() + 20, 1000); // to factor in duplicate entries as 'Zürich' we increase the result size

        CompletionSuggestionBuilder citySuggestions = new CompletionSuggestionBuilder("citySuggestions")
            .prefix(query)
            .size(increasedResultSize);
        CompletionSuggestionBuilder zipCodeSuggestionBuilder = new CompletionSuggestionBuilder("zipCode")
            .prefix(query)
            .size(increasedResultSize);

        return new SuggestBuilder()
            .addSuggestion("cities", citySuggestions)
            .addSuggestion("zipCodes", zipCodeSuggestionBuilder)
            .addSuggestion("cantonCodes", new CompletionSuggestionBuilder("code.canton-suggestions").prefix(query))
            .addSuggestion("cantonNames", new CompletionSuggestionBuilder("cantonSuggestions").prefix(query));
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
}
