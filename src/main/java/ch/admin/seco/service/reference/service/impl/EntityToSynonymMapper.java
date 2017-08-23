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
import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.OccupationSynonym;
import ch.admin.seco.service.reference.domain.search.ClassificationSynonym;
import ch.admin.seco.service.reference.domain.search.LocalitySynonym;
import ch.admin.seco.service.reference.service.dto.ClassificationSuggestionDto;
import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;
import ch.admin.seco.service.reference.service.dto.OccupationSuggestionDto;

@Component
public class EntityToSynonymMapper {

    private final ElasticsearchTemplate elasticsearchTemplate;

    EntityToSynonymMapper(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    ch.admin.seco.service.reference.domain.search.OccupationSynonym toSynonym(OccupationSynonym occupationSynonym) {
        return new ch.admin.seco.service.reference.domain.search.OccupationSynonym()
            .id(occupationSynonym.getId())
            .code(occupationSynonym.getCode())
            .language(occupationSynonym.getLanguage())
            .occupation(occupationSynonym.getName())
            .occupationSuggestions(extractSuggestionList(occupationSynonym.getName()));
    }

    ClassificationSynonym toSynonym(Classification classification) {
        return new ClassificationSynonym()
            .id(classification.getId())
            .code(classification.getCode())
            .language(classification.getLanguage())
            .classification(classification.getName())
            .classificationSuggestions(extractSuggestionList(classification.getName()));
    }

    LocalitySynonym toSynonym(Locality locality) {
        return new LocalitySynonym()
            .id(locality.getId())
            .city(locality.getCity())
            .zipCode(locality.getZipCode())
            .communalCode(locality.getCommunalCode())
            .cantonCode(locality.getCantonCode())
            .geoPoint(locality.getGeoPoint())
            .citySuggestions(extractSuggestionList(locality.getCity()));
    }

    public Locality fromSynonym(LocalitySynonym localitySynonym) {
        return new Locality()
            .id(localitySynonym.getId())
            .city(localitySynonym.getCity())
            .zipCode(localitySynonym.getZipCode())
            .communalCode(localitySynonym.getCommunalCode())
            .cantonCode(localitySynonym.getCantonCode())
            .geoPoint(localitySynonym.getGeoPoint());
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

    LocalitySuggestionDto convertLocalitySuggestion(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new LocalitySuggestionDto()
            .id(String.class.cast(source.get("id")))
            .city(String.class.cast(source.get("city")))
            .communalCode(Integer.class.cast(source.get("communalCode")))
            .cantonCode(String.class.cast(source.get("cantonCode")));
    }
}
