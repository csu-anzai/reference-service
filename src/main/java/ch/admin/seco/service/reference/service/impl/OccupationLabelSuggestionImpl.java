package ch.admin.seco.service.reference.service.impl;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.elasticsearch.search.suggest.SuggestBuilders.completionSuggestion;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.util.CollectionUtils;

import ch.admin.seco.service.reference.domain.OccupationLabel;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.search.OccupationLabelSuggestion;
import ch.admin.seco.service.reference.repository.OccupationLabelRepository;
import ch.admin.seco.service.reference.service.dto.OccupationLabelAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.OccupationLabelSuggestionDto;

/**
 * Service Implementation for suggesting Occupation.
 */
@Service
public class OccupationLabelSuggestionImpl {

    private final Logger LOGGER = LoggerFactory.getLogger(OccupationLabelSuggestionImpl.class);
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final OccupationLabelRepository occupationLabelRepository;
    private final ObjectMapper objectMapper;

    OccupationLabelSuggestionImpl(ElasticsearchTemplate elasticsearchTemplate,
        OccupationLabelRepository occupationLabelRepository) {

        this.elasticsearchTemplate = elasticsearchTemplate;
        this.occupationLabelRepository = occupationLabelRepository;

        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Transactional(readOnly = true)
    public OccupationLabelAutocompleteDto suggest(String prefix, Language language, Collection<String> types, int resultSize) {
        LOGGER.debug("Request to suggest for a page of Occupations for query {}", prefix);

        SearchResponse suggestResponse = elasticsearchTemplate.suggest(
            buildSuggestRequest(prefix, language, types, resultSize),
            OccupationLabelSuggestion.class);

        List<OccupationLabelSuggestionDto> occupations = mapOccupations(suggestResponse, resultSize);
        List<OccupationLabel> classifications = Collections.emptyList();
        if (hasClassificationType(types)) {
            classifications = mapClassifications(suggestResponse, resultSize, occupations, language);
        }
        return new OccupationLabelAutocompleteDto(occupations, classifications);
    }

    boolean hasClassificationType(Collection<String> types) {
        return types.stream().anyMatch(type -> isClassification(type));
    }

    boolean isClassification(String type) {
        return type.startsWith("sbn");
    }

    private List<OccupationLabelSuggestionDto> mapOccupations(SearchResponse suggestResponse, int resultSize) {
        if (Objects.isNull(suggestResponse.getSuggest())) {
            return Collections.emptyList();
        }

        return Stream.concat(
            streamSuggestions(suggestResponse, "occupation"),
            streamSuggestions(suggestResponse, "occupationSuggestions"))
            .distinct()
            .limit(resultSize)
            .collect(toList());
    }

    private List<OccupationLabel> mapClassifications(SearchResponse suggestResponse, int resultSize, List<OccupationLabelSuggestionDto> occupations, Language language) {
        return streamClassifications(suggestResponse, occupations, language)
            .distinct()
            .limit(resultSize)
            .collect(toList());
    }

    private Stream<OccupationLabel> streamClassifications(SearchResponse suggestResponse, List<OccupationLabelSuggestionDto> occupations, Language language) {
        if (Objects.isNull(suggestResponse.getSuggest())) {
            return Stream.empty();
        }

        return Stream.concat(
            streamSuggestions(suggestResponse, "classification")
                .map(dto -> objectMapper.convertValue(dto, OccupationLabel.class)),
            Stream.concat(
                streamSuggestions(suggestResponse, "classificationSuggestions")
                    .map(dto -> objectMapper.convertValue(dto, OccupationLabel.class)),
                streamClassificationsFromOccupations(occupations, language)
            )
                .sorted(Comparator.comparing(OccupationLabel::getLabel))
        );
    }

    private Stream<OccupationLabel> streamClassificationsFromOccupations(List<OccupationLabelSuggestionDto> occupations, Language language) {
        if (CollectionUtils.isEmpty(occupations)) {
            return Stream.empty();
        }
        return occupations.stream()
            .map(OccupationLabelSuggestionDto::getMappings)
            .filter(Objects::nonNull)
            .flatMap(mappings -> mappings.entrySet().stream()
                .filter(mapping -> isClassification(mapping.getKey())))
            .distinct()
            .flatMap(entry -> occupationLabelRepository.findByCodeAndTypeAndLanguage(entry.getValue(), entry.getKey(), language).stream());
    }

    private Stream<OccupationLabelSuggestionDto> streamSuggestions(SearchResponse suggestResponse, String suggestionName) {
        return suggestResponse.getSuggest()
            .<CompletionSuggestion>getSuggestion(suggestionName).getEntries().stream()
            .flatMap(item -> item.getOptions().stream())
            .map(option -> objectMapper.convertValue(option.getHit().getSourceAsMap(), OccupationLabelSuggestionDto.class))
            .sorted(Comparator.comparing(OccupationLabelSuggestionDto::getLabel));
    }

    private SuggestBuilder buildSuggestRequest(String prefix, Language language, Collection<String> types, int resultSize) {
        Map<Type, List<String>> typeMap = types.stream().collect(groupingBy(this::getType));
        List<String> occupationTypes = typeMap.get(Type.OCCUPATION);
        List<String> classificationTypes = typeMap.get(Type.CLASSIFICATION);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        if (!CollectionUtils.isEmpty(occupationTypes)) {
            suggestBuilder
                .addSuggestion("occupation",
                    completionSuggestion("label")
                        .prefix(prefix)
                        .size(resultSize)
                        .contexts(createContext(language, occupationTypes)))
                .addSuggestion("occupationSuggestions",
                    completionSuggestion("occupationSuggestions")
                        .prefix(prefix)
                        .size(resultSize)
                        .contexts(createContext(language, occupationTypes)));
        }
        if (!CollectionUtils.isEmpty(classificationTypes)) {
            suggestBuilder
                .addSuggestion("classification",
                    completionSuggestion("label")
                        .prefix(prefix)
                        .size(resultSize)
                        .contexts(createContext(language, classificationTypes)))
                .addSuggestion("classificationSuggestions",
                    completionSuggestion("occupationSuggestions")
                        .prefix(prefix)
                        .size(resultSize)
                        .contexts(createContext(language, classificationTypes)));
        }
        return suggestBuilder;
    }

    private Type getType(String type) {
        return isClassification(type) ? Type.CLASSIFICATION : Type.OCCUPATION;
    }

    private Map<String, List<? extends ToXContent>> createContext(Language language, Collection<String> types) {
        Objects.requireNonNull(types, "types must not be null");

        return ImmutableMap.of("key",
            types.stream()
                .map(type -> String.format("%s:%s", type, language.name()))
                .map(key -> CategoryQueryContext.builder().setCategory(key).build())
                .collect(toList()));
    }

    enum Type {
        OCCUPATION, CLASSIFICATION
    }
}


