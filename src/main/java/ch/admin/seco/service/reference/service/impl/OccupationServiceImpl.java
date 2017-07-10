package ch.admin.seco.service.reference.service.impl;

import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.search.OccupationSynonym;
import ch.admin.seco.service.reference.repository.OccupationRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSearchRepository;
import ch.admin.seco.service.reference.service.OccupationService;
import ch.admin.seco.service.reference.service.dto.OccupationSuggestionDto;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.context.CategoryQueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Occupation.
 */
@Service
@Transactional
public class OccupationServiceImpl implements OccupationService {

    private final Logger log = LoggerFactory.getLogger(OccupationServiceImpl.class);

    private final OccupationRepository occupationRepository;

    private final OccupationSearchRepository occupationSearchRepository;

    private final ElasticsearchTemplate elasticsearchTemplate;

    public OccupationServiceImpl(OccupationRepository occupationRepository, OccupationSearchRepository occupationSearchRepository, ElasticsearchTemplate elasticsearchTemplate) {
        this.occupationRepository = occupationRepository;
        this.occupationSearchRepository = occupationSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
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

        occupationSearchRepository.deleteAllByCodeEquals(occupation.getCode());
        occupationSearchRepository.saveAll(createSuggestionLists(result));
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
    public List<OccupationSuggestionDto> suggestOccupations(String prefix, Language language, int resultSize) {
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
        return completionSuggestion.getEntries().stream()
            .flatMap(item -> item.getOptions().stream())
            .map(this::convertOccupationSuggestion)
            .collect(Collectors.toList());
    }

    private Collection<OccupationSynonym> createSuggestionLists(Occupation occupation) {
        return occupation.getNamesynonyms().stream()
            .map(name -> new OccupationSynonym()
                .code(occupation.getCode())
                .language(occupation.getLanguage())
                .occupation(name)
                .occupationSuggestions(createSuggestions(name))

            )
            .collect(Collectors.toList());
    }

    private Set<String> createSuggestions(String name) {
        Set<String> suggestions =
            elasticsearchTemplate.getClient()
                .admin()
                .indices()
                .analyze(new AnalyzeRequest().text(name).analyzer("simple"))
                .actionGet()
                .getTokens()
                .stream()
                .map(AnalyzeResponse.AnalyzeToken::getTerm)
                .collect(Collectors.toSet());
        suggestions.add(name);
        return suggestions;
    }

    private OccupationSuggestionDto convertOccupationSuggestion(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new OccupationSuggestionDto(String.class.cast(source.get("occupation")), Integer.class.cast(source.get("code")));
    }
}
