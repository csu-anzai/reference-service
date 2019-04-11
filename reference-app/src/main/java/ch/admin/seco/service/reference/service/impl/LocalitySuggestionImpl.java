package ch.admin.seco.service.reference.service.impl;

import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;
import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.LocalitySearchDto;
import ch.admin.seco.service.reference.service.factory.LocalityAutocompleteConverterFactory;
import ch.admin.seco.service.reference.service.search.CantonSuggestion;
import ch.admin.seco.service.reference.service.search.LocalitySearchRepository;
import ch.admin.seco.service.reference.service.search.LocalitySuggestion;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing Locality.
 */
@Service
@Transactional
public class LocalitySuggestionImpl {

    private final LocalitySearchRepository localitySynonymSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final LocalityToSuggestionMapper entityToSynonymMapper;

    public LocalitySuggestionImpl(LocalitySearchRepository localitySynonymSearchRepository,
                                  ElasticsearchTemplate elasticsearchTemplate,
                                  LocalityToSuggestionMapper entityToSynonymMapper) {

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
        SuggestBuilder localitySuggestionBuilder = createLocalitySuggestBuilder(localitySearchDTO);
        SearchResponse localitiesSearchResponse = elasticsearchTemplate.suggest(localitySuggestionBuilder, LocalitySuggestion.class);
        SuggestBuilder cantonSuggestionsBuilder = createCantonSuggestionBuilder(localitySearchDTO);
        SearchResponse cantonsSearchResponse = elasticsearchTemplate.suggest(cantonSuggestionsBuilder, CantonSuggestion.class);
        return LocalityAutocompleteConverterFactory.getConverter(localitySearchDTO)
            .convert(localitiesSearchResponse, cantonsSearchResponse, localitySearchDTO.getSize());
    }

    private SuggestBuilder createCantonSuggestionBuilder(LocalitySearchDto localitySearchDTO) {
        return new SuggestBuilder()
                .addSuggestion("cantonCodes", new CompletionSuggestionBuilder("code.canton-suggestions").prefix(localitySearchDTO.getQuery()))
                .addSuggestion("cantonNames", new CompletionSuggestionBuilder("cantonSuggestions").prefix(localitySearchDTO.getQuery()));
    }

    private SuggestBuilder createLocalitySuggestBuilder(LocalitySearchDto localitySearchDTO) {
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
            .addSuggestion("zipCodes", zipCodeSuggestionBuilder);
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
        return findOneLocality(query);
    }

    private Optional<Locality> findOneLocality(SearchQuery query) {
        return localitySynonymSearchRepository.search(query)
            .getContent()
            .stream().findFirst()
            .map(entityToSynonymMapper::fromSynonym);
    }

    public Optional<Locality> findByZipCodeAndCity(String zipCode, String city) {
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("zipCode", zipCode))
            .must(QueryBuilders.matchQuery("city", city));

        final NativeSearchQuery query = new NativeSearchQueryBuilder()
            .withFilter(queryBuilder)
            .withPageable(PageRequest.of(0, 1))
            .build();
        return findOneLocality(query);
    }
}
