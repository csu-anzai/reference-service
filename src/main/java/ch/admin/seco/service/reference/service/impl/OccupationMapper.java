package ch.admin.seco.service.reference.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.search.OccupationSynonym;
import ch.admin.seco.service.reference.service.dto.OccupationSuggestionDto;

@Component
public class OccupationMapper {

    private final ElasticsearchTemplate elasticsearchTemplate;

    OccupationMapper(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    OccupationSynonym toOccupationSynonym(Occupation occupation) {
        return new OccupationSynonym()
            .id(occupation.getId())
            .code(occupation.getCode())
            .language(occupation.getLanguage())
            .occupation(occupation.getName())
            .occupationSuggestions(extractSuggestionList(occupation.getName()));
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
}
