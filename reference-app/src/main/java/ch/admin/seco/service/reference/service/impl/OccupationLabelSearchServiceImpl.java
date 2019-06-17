package ch.admin.seco.service.reference.service.impl;

import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import ch.admin.seco.service.reference.domain.OccupationLabel;
import ch.admin.seco.service.reference.domain.OccupationLabelRepository;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;
import ch.admin.seco.service.reference.service.dto.OccupationLabelAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.OccupationLabelSearchRequestDto;
import ch.admin.seco.service.reference.service.dto.OccupationLabelSuggestionDto;
import ch.admin.seco.service.reference.service.search.OccupationLabelSuggestion;

/**
 * Service Implementation for search Occupation.
 */
@Service
@Transactional(readOnly = true)
public class OccupationLabelSearchServiceImpl {

    private final Logger LOGGER = LoggerFactory.getLogger(OccupationLabelSearchServiceImpl.class);
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final OccupationLabelRepository occupationLabelRepository;
    private final ObjectMapper objectMapper;

    OccupationLabelSearchServiceImpl(ElasticsearchTemplate elasticsearchTemplate,
        OccupationLabelRepository occupationLabelRepository) {

        this.elasticsearchTemplate = elasticsearchTemplate;
        this.occupationLabelRepository = occupationLabelRepository;

        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public OccupationLabelAutocompleteDto suggest(String prefix, Language language, Collection<ProfessionCodeType> types, int resultSize) {
        LOGGER.debug("Request to suggest for a page of Occupations for query {}", prefix);

        SearchResponse suggestResponse = elasticsearchTemplate.suggest(
            new OccupationLabelSearchQueryBuilder().buildSuggestQuery(prefix, language, types, resultSize),
            OccupationLabelSuggestion.class);

        List<OccupationLabelSuggestionDto> occupations = mapOccupations(suggestResponse, resultSize);
        List<OccupationLabel> classifications = Collections.emptyList();
        if (ProfessionCodeType.hasClassificationType(types)) {
            classifications = mapClassifications(suggestResponse, resultSize, occupations, language);
        }
        return new OccupationLabelAutocompleteDto(occupations, classifications);
    }

    public Optional<OccupationLabelSuggestionDto> findOneByCodeTypeLanguageClassifier(ProfessionCodeType type, String code, Language language, String classifier) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
            .filter(termQuery("type", type.toString()))
            .filter(termQuery("code", code))
            .filter(termQuery("language", language.toString()))
            .filter(termQuery("classifier", classifier));


        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
            .withQuery(queryBuilder)
            .build();
        Page<OccupationLabelSuggestion> result = this.elasticsearchTemplate.queryForPage(nativeSearchQuery, OccupationLabelSuggestion.class);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        OccupationLabelSuggestion occupationLabelSuggestion = result.getContent().get(0);
        OccupationLabelSuggestionDto occupationLabelSuggestionDto = new OccupationLabelSuggestionDto()
            .setId(occupationLabelSuggestion.getId())
            .setCode(occupationLabelSuggestion.getCode())
            .setType(occupationLabelSuggestion.getType())
            .setClassifier(occupationLabelSuggestion.getClassifier())
            .setLanguage(occupationLabelSuggestion.getLanguage())
            .setMappings(occupationLabelSuggestion.getMappings())
            .setLabel(occupationLabelSuggestion.getLabel());

        return Optional.of(occupationLabelSuggestionDto);
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
                .filter(mapping -> ProfessionCodeType.isClassification(mapping.getKey())))
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

    public Page<OccupationLabel> search(OccupationLabelSearchRequestDto searchRequest, Language language) {
        final SearchQuery searchQuery = new OccupationLabelSearchQueryBuilder()
            .buildSearchQuery(searchRequest, language);
        return elasticsearchTemplate.queryForPage(searchQuery, OccupationLabelSuggestion.class)
            .map(occupationLabelSuggestion -> objectMapper.convertValue(occupationLabelSuggestion, OccupationLabel.class));
    }
}


