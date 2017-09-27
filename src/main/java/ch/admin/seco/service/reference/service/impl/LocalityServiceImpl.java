package ch.admin.seco.service.reference.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.search.LocalitySynonym;
import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;
import ch.admin.seco.service.reference.repository.LocalityRepository;
import ch.admin.seco.service.reference.repository.search.LocalitySynonymSearchRepository;
import ch.admin.seco.service.reference.service.LocalityService;
import ch.admin.seco.service.reference.service.dto.CantonSuggestionDto;
import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;

/**
 * Service Implementation for managing Locality.
 */
@Service
@Transactional
public class LocalityServiceImpl implements LocalityService {

    private final Logger log = LoggerFactory.getLogger(LocalityServiceImpl.class);

    private final LocalityRepository localityRepository;
    private final LocalitySynonymSearchRepository localitySynonymSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EntityToSynonymMapper entityToSynonymMapper;

    public LocalityServiceImpl(LocalityRepository localityRepository,
        LocalitySynonymSearchRepository localitySynonymSearchRepository,
        ElasticsearchTemplate elasticsearchTemplate,
        EntityToSynonymMapper entityToSynonymMapper) {
        this.localityRepository = localityRepository;
        this.localitySynonymSearchRepository = localitySynonymSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.entityToSynonymMapper = entityToSynonymMapper;
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
        index(locality);
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
                localitySynonymSearchRepository.deleteById(locality.getId());
            });
    }

    /**
     * Search for the locality corresponding
     * to the prefix and limit result by resultSize.
     *
     * @param prefix the prefix of the locality search
     * @param resultSize the response size information
     * @return the result of the search
     */
    @Override
    public LocalityAutocompleteDto search(String prefix, int resultSize) {
        CompletionSuggestionBuilder citySuggestions = new CompletionSuggestionBuilder("citySuggestions")
            .prefix(prefix)
            .size(Math.min(resultSize + 20, 10000)); // to factor in duplicate entries as 'Zürich' we increase the result size

        SuggestBuilder suggestBuilder = new SuggestBuilder()
            .addSuggestion("localities", citySuggestions)
            .addSuggestion("cantonCodes", new CompletionSuggestionBuilder("code.canton-suggestions").prefix(prefix))
            .addSuggestion("cantonNames", new CompletionSuggestionBuilder("name.canton-suggestions").prefix(prefix));
        SearchResponse searchResponse = elasticsearchTemplate.suggest(suggestBuilder, LocalitySynonym.class);

        List<LocalitySuggestionDto> localities = convertSuggestionToDto(resultSize, searchResponse, entityToSynonymMapper::convertLocalitySuggestion, "localities");

        List<CantonSuggestionDto> cantons = convertSuggestionToDto(resultSize, searchResponse, entityToSynonymMapper::convertCantonSuggestion, "cantonCodes", "cantonNames");

        return new LocalityAutocompleteDto(localities, cantons);
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
        return localitySynonymSearchRepository.search(query)
            .getContent()
            .stream().findFirst()
            .map(entityToSynonymMapper::fromSynonym);
    }

    @Override
    public List<Locality> findOneByZipCode(String zipCode) {
        return localityRepository.findByZipCode(zipCode);
    }

    @Async
    public void index(Locality locality) {
        localitySynonymSearchRepository.index(entityToSynonymMapper.toSuggestion(locality));
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
