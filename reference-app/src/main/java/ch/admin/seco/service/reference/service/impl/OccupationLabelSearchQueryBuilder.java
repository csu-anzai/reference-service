package ch.admin.seco.service.reference.service.impl;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.suggest.SuggestBuilders.completionSuggestion;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.context.CategoryQueryContext;

import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.util.CollectionUtils;

import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;
import ch.admin.seco.service.reference.service.dto.OccupationLabelSearchRequestDto;

public class OccupationLabelSearchQueryBuilder {

    public SuggestBuilder buildSuggestQuery(String prefix, Language language, Collection<ProfessionCodeType> types, int resultSize) {
        Map<Type, List<ProfessionCodeType>> typeMap = types.stream().collect(groupingBy(this::getType));
        List<ProfessionCodeType> occupationTypes = typeMap.get(Type.OCCUPATION);
        List<ProfessionCodeType> classificationTypes = typeMap.get(Type.CLASSIFICATION);

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

    private Type getType(ProfessionCodeType type) {
        return ProfessionCodeType.isClassification(type)
            ? Type.CLASSIFICATION
            : Type.OCCUPATION;
    }

    private Map<String, List<? extends ToXContent>> createContext(Language language, Collection<ProfessionCodeType> types) {
        Objects.requireNonNull(types, "types must not be null");

        return ImmutableMap.of("key",
            types.stream()
                .map(type -> String.format("%s:%s", type.name(), language.name()))
                .map(key -> CategoryQueryContext.builder().setCategory(key).build())
                .collect(toList()));
    }

    public SearchQuery buildSearchQuery(OccupationLabelSearchRequestDto searchRequest, Language language) {
        return new NativeSearchQueryBuilder()
            .withPageable(searchRequest.getPageable())
            .withQuery(buildSearchQuery(searchRequest.getPrefix()))
            .withFilter(buildSearchFilter(searchRequest, language))
            .withSort(SortBuilders.fieldSort("label.raw"))
            .build();
    }

    private QueryBuilder buildSearchQuery(String prefix) {
        if (StringUtils.isEmpty(prefix)) {
            return QueryBuilders.matchAllQuery();
        }

        return prefixQuery("label.raw", prefix);
    }

    private QueryBuilder buildSearchFilter(OccupationLabelSearchRequestDto searchRequest, Language language) {
        return boolQuery()
            .must(termQuery("language", language.name()))
            .must(termQuery("type", searchRequest.getCodeType().name()));
    }

    enum Type {
        OCCUPATION, CLASSIFICATION
    }
}
