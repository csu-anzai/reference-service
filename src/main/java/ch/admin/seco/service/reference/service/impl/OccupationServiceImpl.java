package ch.admin.seco.service.reference.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.context.CategoryQueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.OccupationMapping;
import ch.admin.seco.service.reference.domain.OccupationSynonym;
import ch.admin.seco.service.reference.repository.OccupationMappingRepository;
import ch.admin.seco.service.reference.repository.OccupationRepository;
import ch.admin.seco.service.reference.repository.OccupationSynonymRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSynonymSearchRepository;
import ch.admin.seco.service.reference.service.OccupationService;
import ch.admin.seco.service.reference.service.dto.ClassificationSuggestionDto;
import ch.admin.seco.service.reference.service.dto.OccupationAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.OccupationSuggestionDto;

/**
 * Service Implementation for managing Occupation.
 */
@Service
@Transactional
public class OccupationServiceImpl implements OccupationService {

    private final Logger log = LoggerFactory.getLogger(OccupationServiceImpl.class);
    private final ApplicationContext applicationContext;
    private final OccupationSynonymRepository occupationSynonymRepository;
    private final OccupationSynonymSearchRepository occupationSynonymSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EntityToSynonymMapper occupationSynonymMapper;
    private final OccupationMappingRepository occupationMappingRepository;
    private final OccupationRepository occupationRepository;
    private final Function<OccupationMapping, Optional<Occupation>> occupationMappingToOccupation;
    private OccupationServiceImpl occupationServiceImpl;

    public OccupationServiceImpl(ApplicationContext applicationContext,
        OccupationSynonymRepository occupationSynonymRepository,
        OccupationSynonymSearchRepository occupationSynonymSearchRepository,
        ElasticsearchTemplate elasticsearchTemplate,
        EntityToSynonymMapper occupationSynonymMapper,
        OccupationMappingRepository occupationMappingRepository,
        OccupationRepository occupationRepository) {

        this.applicationContext = applicationContext;
        this.occupationSynonymRepository = occupationSynonymRepository;
        this.occupationSynonymSearchRepository = occupationSynonymSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.occupationSynonymMapper = occupationSynonymMapper;
        this.occupationMappingRepository = occupationMappingRepository;
        this.occupationRepository = occupationRepository;

        this.occupationMappingToOccupation = mapping -> occupationRepository.findOneByCode(mapping.getCode());
    }

    /**
     * Save a occupationSynonym.
     *
     * @param occupationSynonym the entity to save
     * @return the persisted entity
     */
    @Override
    public OccupationSynonym save(OccupationSynonym occupationSynonym) {
        log.debug("Request to save OccupationSynonym : {}", occupationSynonym);
        OccupationSynonym result = occupationSynonymRepository.save(occupationSynonym);
        occupationServiceImpl.index(result);
        return result;
    }

    /**
     * Get all the occupationSynonyms.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OccupationSynonym> findAll(Pageable pageable) {
        log.debug("Request to get all OccupationSynonyms");
        return occupationSynonymRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OccupationSynonym> findOne(UUID id) {
        log.debug("Request to get OccupationSynonym : {}", id);
        return occupationSynonymRepository.findById(id);
    }

    /**
     * Delete the occupationSynonym by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete OccupationSynonym : {}", id);
        occupationSynonymRepository.findById(id).ifPresent(
            occupation -> {
                occupationSynonymRepository.delete(occupation);
                occupationSynonymSearchRepository.deleteAllByCodeEquals(occupation.getCode());
            }
        );
    }

    /**
     * Search for the occupation synonym corresponding to the query.
     *
     * @param prefix   the query of the search
     * @param language the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public OccupationAutocompleteDto suggestOccupationSynonyms(String prefix, Language language, int resultSize) {
        log.debug("Request to search for a page of Occupations for query {}", prefix);

        Function<String, SuggestionBuilder> createSuggestionBuilder = (String field) ->
            SuggestBuilders.completionSuggestion(field + "." + language.name())
                .prefix(prefix, Fuzziness.AUTO)
                .size(resultSize)
                .contexts(Collections.singletonMap("lang",
                    Collections.singletonList(CategoryQueryContext.builder().setCategory(language.name()).build())));

        SearchResponse suggestResponse = elasticsearchTemplate.suggest(new SuggestBuilder()
                .addSuggestion("occupations", createSuggestionBuilder.apply("occupationSuggestions"))
                .addSuggestion("classifications", createSuggestionBuilder.apply("classificationSuggestions")),
            ch.admin.seco.service.reference.domain.search.OccupationSynonym.class);

        List<OccupationSuggestionDto> occupations = suggestResponse.getSuggest()
            .<CompletionSuggestion>getSuggestion("occupations").getEntries().stream()
            .flatMap(item -> item.getOptions().stream())
            .map(occupationSynonymMapper::convertOccupationSuggestion)
            .collect(Collectors.toList());

        List<ClassificationSuggestionDto> classifications = suggestResponse.getSuggest()
            .<CompletionSuggestion>getSuggestion("classifications").getEntries().stream()
            .flatMap(item -> item.getOptions().stream())
            .map(occupationSynonymMapper::convertClassificationSuggestion)
            .collect(Collectors.toList());

        return new OccupationAutocompleteDto(occupations, classifications);
    }

    /**
     * Get one occupationMapping by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<OccupationMapping> findOneOccupationMapping(UUID id) {
        log.debug("Request to get OccupationMapping : {}", id);
        return occupationMappingRepository.findById(id);
    }

    /**
     * Get all the occupationMappings.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OccupationMapping> findAllOccupationMappings(Pageable pageable) {
        log.debug("Request to get all OccupationMappings");
        return occupationMappingRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Occupation> findOneOccupationByCode(int code) {
        log.debug("Request to get OccupationMapping : code:{}", code);
        return occupationRepository.findOneByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Occupation> findOneOccupationByAvamCode(int avamCode) {
        log.debug("Request to get OccupationMapping : avamCode:{}", avamCode);
        return occupationMappingRepository.findByAvamCode(avamCode)
            .stream()
            .findFirst()
            .flatMap(occupationMappingToOccupation);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Occupation> findOneOccupationByX28Code(int x28Code) {
        log.debug("Request to get OccupationMapping : x28Code:{}", x28Code);
        return occupationMappingRepository.findByX28Code(x28Code)
            .flatMap(occupationMappingToOccupation);
    }

    /**
     * Get one occupationSynonym by id.
     *
     * @param externalId the externalId of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<OccupationSynonym> findOneOccupationSynonymByExternalId(int externalId) {
        log.debug("Request to get OccupationSynonym : {}", externalId);
        return occupationSynonymRepository.findByExternalId(externalId);
    }

    @Async
    void index(OccupationSynonym occupationSynonym) {
        occupationSynonymSearchRepository.save(occupationSynonymMapper.toSynonym(occupationSynonym));
    }

    @PostConstruct
    private void init() {
        occupationServiceImpl = applicationContext.getBean(OccupationServiceImpl.class);
    }
}
