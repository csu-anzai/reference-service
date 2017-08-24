package ch.admin.seco.service.reference.service.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Locality fromSynonym(LocalitySynonym localitySynonym) {
        return new Locality()
            .id(localitySynonym.getId())
            .city(localitySynonym.getCity())
            .zipCode(localitySynonym.getZipCode())
            .communalCode(localitySynonym.getCommunalCode())
            .cantonCode(localitySynonym.getCantonCode())
            .geoPoint(localitySynonym.getGeoPoint());
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

    Set<String> extractSuggestionList(String term) {
        Set<String> suggestions = new HashSet<>();
        suggestions.add(term);
        Pattern pattern = Pattern.compile("[-_/\\\\. ]+");

        nextSubTerm(term, suggestions, pattern);

        suggestions.remove("");
        suggestions.remove(null);
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
            .city(String.class.cast(source.get("city")))
            .communalCode(Integer.class.cast(source.get("communalCode")))
            .cantonCode(String.class.cast(source.get("cantonCode")));
    }

    private void nextSubTerm(String term, Set<String> suggestions, Pattern pattern) {
        Matcher matcher = pattern.matcher(term);
        if (matcher.find()) {
            term = term.substring(matcher.end());
            suggestions.add(term);
            nextSubTerm(term, suggestions, pattern);
        }
    }
}
