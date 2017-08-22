package ch.admin.seco.service.reference.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;
import ch.admin.seco.service.reference.repository.LocalityRepository;
import ch.admin.seco.service.reference.repository.search.LocalitySearchRepository;
import ch.admin.seco.service.reference.service.LocalityService;
import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;

/**
 * Service Implementation for managing Locality.
 */
@Service
@Transactional
public class LocalityServiceImpl implements LocalityService {

    private final Logger log = LoggerFactory.getLogger(LocalityServiceImpl.class);

    private final LocalityRepository localityRepository;
    private final LocalitySearchRepository localitySearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EntityToSynonymMapper occupationSynonymMapper;

    public LocalityServiceImpl(LocalityRepository localityRepository,
        LocalitySearchRepository localitySearchRepository,
        ElasticsearchTemplate elasticsearchTemplate,
        EntityToSynonymMapper occupationSynonymMapper) {
        this.localityRepository = localityRepository;
        this.localitySearchRepository = localitySearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.occupationSynonymMapper = occupationSynonymMapper;
    }

    /**
     * Save a locality.
     *
     * @param locality the entity to save
     * @return the persisted entity
     */
    @Override
    public Locality save(Locality locality) {
        log.debug("Request to save Locality : {}", locality);
        Locality result = localityRepository.save(locality);
        localitySearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the localities.
     *
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<Locality> findAll() {
        log.debug("Request to get all Localities");
        return localityRepository.findAll();
    }

    /**
     *  Get one locality by id.
     *
     *  @param id the uuid of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Locality> findOne(UUID id) {
        log.debug("Request to get Locality : {}", id);
        return localityRepository.findById(id);
    }

    /**
     *  Delete the  locality by id.
     *
     *  @param id the uuid of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Locality : {}", id);
        localityRepository.findById(id).ifPresent(
            locality -> {
                localityRepository.delete(locality);
                localitySearchRepository.delete(locality);
            });
    }

    /**
     * Search for the locality corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<Locality> search(String query) {
        log.debug("Request to search Localities for query {}", query);
        return StreamSupport
            .stream(localitySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

    /**
     * Search for the nearest locality to the geo point.
     *
     * @param geoPoint the geo point from which to search
     * @return nearest entity to geo point
     */
    @Override
    public Optional<Locality> searchNearestLocality(GeoPoint geoPoint) {
        GeoDistanceSortBuilder distanceSortBuilder = SortBuilders.geoDistanceSort("geoPoint", geoPoint.getLatitude(), geoPoint.getLongitude())
            .unit(DistanceUnit.KILOMETERS)
            .geoDistance(GeoDistance.PLANE);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
            .withSort(distanceSortBuilder)
            .withPageable(PageRequest.of(0, 1))
            .build();
        return localitySearchRepository.search(query)
            .getContent()
            .stream()
            .findFirst();
    }

    @Override
    public List<LocalitySuggestionDto> suggestLocalities(String prefix, int resultSize) {
        CompletionSuggestionBuilder citySuggestions = new CompletionSuggestionBuilder("citySuggestions")
            .prefix(prefix, Fuzziness.AUTO)
            .size(resultSize);
        SuggestBuilder suggestBuilder = new SuggestBuilder().addSuggestion("localities", citySuggestions);
        SearchResponse searchResponse = elasticsearchTemplate.suggest(suggestBuilder, Locality.class);

        return searchResponse.getSuggest()
            .<CompletionSuggestion>getSuggestion("localities")
            .getEntries().stream()
            .flatMap(item -> item.getOptions().stream())
            .map(occupationSynonymMapper::convertLocalitySuggestion)
            .collect(Collectors.toList());
    }
}
