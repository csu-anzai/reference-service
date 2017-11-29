package ch.admin.seco.service.reference.service.impl;

import static java.util.stream.Collectors.toList;
import static org.elasticsearch.search.suggest.SuggestBuilders.completionSuggestion;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            buildSuggestRequest(prefix, language, types, resultSize * 2),
            OccupationLabelSuggestion.class);

        Collection<OccupationLabelSuggestionDto> occupationSuggestionDtos = mapOccupations(suggestResponse, resultSize);
        Collection<OccupationLabel> classifications = occupationSuggestionDtos.stream()
            .map(OccupationLabelSuggestionDto::getMappings)
            .filter(Objects::nonNull)
            .flatMap(mappings -> mappings.entrySet().stream()
                .filter(a -> a.getKey().startsWith("sbn")))
            .distinct()
            .flatMap(entry -> occupationLabelRepository.findByCodeAndTypeAndLanguage(entry.getValue(), entry.getKey(), language).stream())
            .sorted(Comparator.comparing(OccupationLabel::getLabel))
            .collect(toList());

        return new OccupationLabelAutocompleteDto(occupationSuggestionDtos, classifications);
    }

    private Collection<OccupationLabelSuggestionDto> mapOccupations(SearchResponse suggestResponse, int resultSize) {
        if (Objects.isNull(suggestResponse.getSuggest())) {
            return Collections.emptyList();
        }

        return suggestResponse.getSuggest()
            .<CompletionSuggestion>getSuggestion("occupation").getEntries().stream()
            .flatMap(item -> item.getOptions().stream())
            .map(option -> objectMapper.convertValue(option.getHit().getSourceAsMap(), OccupationLabelSuggestionDto.class))
            .distinct()
            .limit(resultSize)
            .sorted(Comparator.comparing(OccupationLabelSuggestionDto::getLabel))
            .collect(toList());
    }

    private SuggestBuilder buildSuggestRequest(String prefix, Language language, Collection<String> types, int resultSize) {
        return new SuggestBuilder()
            .addSuggestion("occupation",
                completionSuggestion("occupationSuggestions")
                    .prefix(prefix)
                    .size(resultSize)
                    .contexts(createContext(language, types))
            );
    }

    private Map<String, List<? extends ToXContent>> createContext(Language language, Collection<String> types) {
        return ImmutableMap.of("key",
            types.stream()
                .map(type -> String.format("%s:%s", type, language.name()))
                .map(key -> CategoryQueryContext.builder().setCategory(key).build())
                .collect(toList()));
    }
}


