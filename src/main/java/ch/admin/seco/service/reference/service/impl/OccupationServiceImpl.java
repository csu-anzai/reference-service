package ch.admin.seco.service.reference.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
import ch.admin.seco.service.reference.domain.search.OccupationSynonym;
import ch.admin.seco.service.reference.repository.OccupationRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSearchRepository;
import ch.admin.seco.service.reference.service.OccupationService;
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
    private final OccupationRepository occupationRepository;
    private final OccupationSearchRepository occupationSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final OccupationMapper occupationMapper;
    private OccupationServiceImpl occupationServiceImpl;

    public OccupationServiceImpl(ApplicationContext applicationContext, OccupationRepository occupationRepository, OccupationSearchRepository occupationSearchRepository, ElasticsearchTemplate elasticsearchTemplate, OccupationMapper occupationMapper) {
        this.applicationContext = applicationContext;
        this.occupationRepository = occupationRepository;
        this.occupationSearchRepository = occupationSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.occupationMapper = occupationMapper;
    }

    /**
     * Save a occupation.
     *
     * @param occupation the entity to save
     * @return the persisted entity
     */
    @Override
    public Occupation save(Occupation occupation) {
        log.debug("Request to save Occupation : {}", occupation);
        Occupation result = occupationRepository.save(occupation);
        occupationServiceImpl.index(result);
        return result;
    }

    /**
     * Get all the occupations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Occupation> findAll(Pageable pageable) {
        log.debug("Request to get all Occupations");
        return occupationRepository.findAll(pageable);
    }

    /**
     * Get one occupation by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Occupation> findOne(UUID id) {
        log.debug("Request to get Occupation : {}", id);
        return occupationRepository.findById(id);
    }

    /**
     * Delete the  occupation by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Occupation : {}", id);
        occupationRepository.findById(id).ifPresent(
            occupation -> {
                occupationRepository.delete(occupation);
                occupationSearchRepository.deleteAllByCodeEquals(occupation.getCode());
            }
        );
    }

    /**
     * Search for the occupation corresponding to the query.
     *
     * @param prefix   the query of the search
     * @param language the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public OccupationAutocompleteDto suggestOccupations(String prefix, Language language, int resultSize) {
        log.debug("Request to search for a page of Occupations for query {}", prefix);

        SuggestionBuilder completionSuggestionFuzzyBuilder =
            SuggestBuilders.completionSuggestion("occupationSuggestions." + language.name())
                .prefix(prefix, Fuzziness.AUTO)
                .size(resultSize)
                .contexts(Collections.singletonMap("lang",
                    Collections.singletonList(CategoryQueryContext.builder().setCategory(language.name()).build())));

        SearchResponse suggestResponse = elasticsearchTemplate.suggest(new SuggestBuilder()
            .addSuggestion("occupations", completionSuggestionFuzzyBuilder), OccupationSynonym.class);

        CompletionSuggestion completionSuggestion = suggestResponse.getSuggest().getSuggestion("occupations");
        List<OccupationSuggestionDto> occupations = completionSuggestion.getEntries().stream()
            .flatMap(item -> item.getOptions().stream())
            .map(occupationMapper::convertOccupationSuggestion)
            .collect(Collectors.toList());

        return new OccupationAutocompleteDto(occupations, null);
    }

    @Async
    void index(Occupation occupation) {
        occupationSearchRepository.save(occupationMapper.toOccupationSynonym(occupation));
    }

    @PostConstruct
    private void init() {
        occupationServiceImpl = applicationContext.getBean(OccupationServiceImpl.class);
    }

}
