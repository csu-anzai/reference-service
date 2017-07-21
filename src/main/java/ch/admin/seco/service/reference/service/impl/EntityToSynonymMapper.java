package ch.admin.seco.service.reference.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import ch.admin.seco.service.reference.domain.Classification;
import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.search.ClassificationSynonym;
import ch.admin.seco.service.reference.domain.search.OccupationSynonym;
import ch.admin.seco.service.reference.service.dto.ClassificationSuggestionDto;
import ch.admin.seco.service.reference.service.dto.OccupationSuggestionDto;

@Component
public class EntityToSynonymMapper {

    private final ElasticsearchTemplate elasticsearchTemplate;

    EntityToSynonymMapper(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    OccupationSynonym toSynonym(Occupation occupation) {
        return new OccupationSynonym()
            .id(occupation.getId())
            .code(occupation.getCode())
            .language(occupation.getLanguage())
            .occupation(occupation.getName())
            .occupationSuggestions(extractSuggestionList(occupation.getName()));
    }

    ClassificationSynonym toSynonym(Classification classification) {
        return new ClassificationSynonym()
            .id(classification.getId())
            .code(classification.getCode())
            .language(classification.getLanguage())
            .classification(classification.getName())
            .classificationSuggestions(extractSuggestionList(classification.getName()));
    }

    Set<String> extractSuggestionList(String name) {
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
        suggestions.add(name.toLowerCase());
        return suggestions;
    }

    OccupationSuggestionDto convertOccupationSuggestion(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new OccupationSuggestionDto(String.class.cast(source.get("occupation")), Integer.class.cast(source.get("code")));
    }

    ClassificationSuggestionDto convertClassificationSuggestion(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new ClassificationSuggestionDto(String.class.cast(source.get("classification")), Integer.class.cast(source.get("code")));
    }
}
