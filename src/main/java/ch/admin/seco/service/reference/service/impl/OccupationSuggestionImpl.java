package ch.admin.seco.service.reference.service.impl;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.elasticsearch.search.suggest.SuggestBuilders.completionSuggestion;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.context.CategoryQueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.Classification;
import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.search.OccupationSynonymSuggestion;
import ch.admin.seco.service.reference.repository.ClassificationRepository;
import ch.admin.seco.service.reference.service.dto.ClassificationSuggestionDto;
import ch.admin.seco.service.reference.service.dto.OccupationAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.OccupationSuggestionDto;

/**
 * Service Implementation for suggesting Occupation.
 */
@Service
public class OccupationSuggestionImpl {

    private final Logger LOGGER = LoggerFactory.getLogger(OccupationSuggestionImpl.class);
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EntityToSynonymMapper occupationSynonymMapper;
    private final ClassificationRepository classificationRepository;

    public OccupationSuggestionImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        EntityToSynonymMapper occupationSynonymMapper,
        ClassificationRepository classificationRepository) {

        this.elasticsearchTemplate = elasticsearchTemplate;
        this.occupationSynonymMapper = occupationSynonymMapper;
        this.classificationRepository = classificationRepository;
    }

    /**
     * Search for the occupation synonym corresponding to the query.
     *
     * @param prefix   the query of the suggest
     * @param language the pagination information
     * @param resultSize the size of the resultList
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public OccupationAutocompleteDto suggest(String prefix, Language language, int resultSize) {
        LOGGER.debug("Request to suggest for a page of Occupations for query {}", prefix);

        SearchResponse suggestResponse = elasticsearchTemplate.suggest(
            buildSuggestRequest(prefix, language, resultSize),
            OccupationSynonymSuggestion.class);

        List<OccupationSuggestionDto> occupations = mapOccupations(suggestResponse);
        List<ClassificationSuggestionDto> classifications = mapClassifications(language, suggestResponse, occupations);

        return new OccupationAutocompleteDto(occupations, classifications);
    }

    private List<ClassificationSuggestionDto> mapClassifications(Language language, SearchResponse suggestResponse, List<OccupationSuggestionDto> occupations) {
        return Stream.of(
            suggestResponse.getSuggest()
                .<CompletionSuggestion>getSuggestion("classifications").getEntries().stream()
                .flatMap(item -> item.getOptions().stream())
                .map(option -> occupationSynonymMapper.convertClassificationSuggestion(option, language)),
            getClassificationsFromOccupatonAsStream(occupations)
                .map(classification -> new ClassificationSuggestionDto(classification.getLabels().get(language), classification.getCode())))
            .flatMap(Function.identity())
            .distinct()
            .collect(Collectors.toList());
    }

    private List<OccupationSuggestionDto> mapOccupations(SearchResponse suggestResponse) {
        return suggestResponse.getSuggest()
            .<CompletionSuggestion>getSuggestion("occupations").getEntries().stream()
            .flatMap(item -> item.getOptions().stream())
            .map(occupationSynonymMapper::convertOccupationSuggestion)
            .collect(Collectors.toList());
    }

    private SuggestBuilder buildSuggestRequest(String prefix, Language language, int resultSize) {
        return new SuggestBuilder()
            .addSuggestion("occupations",
                completionSuggestion("occupationSuggestions." + language.name())
                    .prefix(prefix)
                    .size(resultSize)
                    .contexts(singletonMap("lang",
                        singletonList(CategoryQueryContext.builder()
                            .setCategory(language.name())
                            .build())))
            )
            .addSuggestion("classifications",
                completionSuggestion("classificationSuggestions." + language.name())
                    .prefix(prefix)
                    .size(resultSize)
            );
    }

    private Stream<Classification> getClassificationsFromOccupatonAsStream(List<OccupationSuggestionDto> occupations) {
        List<Integer> occupationCodes = occupations.stream()
            .map(occupationSuggestionDto -> occupationSuggestionDto.getCode())
            .collect(Collectors.toList());
        return classificationRepository.findAllByOccupationCodes(occupationCodes);
    }
}


