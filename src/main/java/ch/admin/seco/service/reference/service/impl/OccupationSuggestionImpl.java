package ch.admin.seco.service.reference.service.impl;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static org.elasticsearch.search.suggest.SuggestBuilders.completionSuggestion;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.context.CategoryQueryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.Classification;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.search.OccupationSuggestion;
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
    private final EntityToSuggestionMapper occupationSynonymMapper;
    private final ClassificationRepository classificationRepository;

    OccupationSuggestionImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        EntityToSuggestionMapper occupationSynonymMapper,
        ClassificationRepository classificationRepository) {

        this.elasticsearchTemplate = elasticsearchTemplate;
        this.occupationSynonymMapper = occupationSynonymMapper;
        this.classificationRepository = classificationRepository;
    }

    @Transactional(readOnly = true)
    public OccupationAutocompleteDto suggest(String prefix, Language language, boolean includeSynonyms, int resultSize) {
        LOGGER.debug("Request to suggest for a page of Occupations for query {}", prefix);

        SearchResponse suggestResponse = elasticsearchTemplate.suggest(
            buildSuggestRequest(prefix, language, includeSynonyms, resultSize * 2),
            OccupationSuggestion.class);

        List<OccupationSuggestionDto> occupations = mapOccupations(suggestResponse, resultSize);
        List<ClassificationSuggestionDto> classifications = mapClassifications(language, suggestResponse, occupations);

        return new OccupationAutocompleteDto(occupations, classifications);
    }

    private List<ClassificationSuggestionDto> mapClassifications(Language language, SearchResponse suggestResponse, List<OccupationSuggestionDto> occupations) {
        return Stream.of(
            suggestResponse.getSuggest()
                .<CompletionSuggestion>getSuggestion("classification").getEntries().stream()
                .flatMap(item -> item.getOptions().stream())
                .map(option -> occupationSynonymMapper.toClassificationSuggestion(option, language)),
            getClassificationsFromOccupationAsStream(occupations)
                .map(classification -> new ClassificationSuggestionDto(classification.getLabels().get(language), classification.getCode())))
            .flatMap(Function.identity())
            .distinct()
            .sorted((a, b) -> a.getName().compareTo(b.getName()))
            .collect(Collectors.toList());
    }

    private List<OccupationSuggestionDto> mapOccupations(SearchResponse suggestResponse, int resultSize) {
        return suggestResponse.getSuggest()
            .<CompletionSuggestion>getSuggestion("occupation").getEntries().stream()
            .flatMap(item -> item.getOptions().stream())
            .map(occupationSynonymMapper::convertOccupationSuggestion)
            .distinct()
            .limit(resultSize)
            .sorted((a, b) -> a.getName().compareTo(b.getName()))
            .collect(Collectors.toList());
    }

    private SuggestBuilder buildSuggestRequest(String prefix, Language language, boolean includeSynonyms, int resultSize) {
        return new SuggestBuilder()
            .addSuggestion("occupation",
                completionSuggestion("occupationSuggestions." + language.name())
                    .prefix(prefix)
                    .size(resultSize)
                    .contexts(createContext(language, includeSynonyms))
            )
            .addSuggestion("classification",
                completionSuggestion("classificationSuggestions." + language.name())
                    .prefix(prefix)
                    .size(resultSize)
            );
    }

    private Map<String, List<? extends ToXContent>> createContext(Language language, boolean includeSynonyms) {
        if (includeSynonyms) {
            return ImmutableMap.of("lang", singletonList(CategoryQueryContext.builder()
                .setCategory(language.name())
                .build()));
        } else {
            return ImmutableMap.of("lang_syno", singletonList(CategoryQueryContext.builder()
                .setCategory(language.name() + "_false")
                .build()));
        }
    }

    private Stream<Classification> getClassificationsFromOccupationAsStream(List<OccupationSuggestionDto> occupations) {
        return classificationRepository.findAllByOccupationCodes(occupations.stream()
            .map(OccupationSuggestionDto::getCode)
            .collect(toSet()));
    }
}


