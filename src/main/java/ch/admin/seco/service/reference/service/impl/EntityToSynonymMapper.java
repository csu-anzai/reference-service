package ch.admin.seco.service.reference.service.impl;

import static java.util.Objects.isNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Component;

import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.OccupationSynonym;
import ch.admin.seco.service.reference.domain.search.ClassificationSuggestion;
import ch.admin.seco.service.reference.domain.search.LocalitySynonym;
import ch.admin.seco.service.reference.domain.search.OccupationSynonymSuggestion;
import ch.admin.seco.service.reference.domain.search.Suggestions;
import ch.admin.seco.service.reference.domain.valueobject.Labels;
import ch.admin.seco.service.reference.service.dto.CantonSuggestionDto;
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

    OccupationSynonymSuggestion toSuggestion(OccupationSynonym occupationSynonym) {
        return new OccupationSynonymSuggestion()
            .id(occupationSynonym.getId())
            .code(occupationSynonym.getCode())
            .language(occupationSynonym.getLanguage())
            .name(occupationSynonym.getName())
            .occupationSuggestions(extractSuggestionList(occupationSynonym.getName()));
    }

    ClassificationSuggestion toSuggestion(ch.admin.seco.service.reference.domain.Classification classification) {
        return new ClassificationSuggestion()
            .id(classification.getId())
            .code(classification.getCode())
            .labels(classification.getLabels())
            .classificationSuggestions(extractSuggestions(classification.getLabels()));
    }

    LocalitySynonym toSuggestion(Locality locality) {
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
        if (isNull(term)) {
            return Collections.emptySet();
        }
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
        return new OccupationSuggestionDto(String.class.cast(source.get("name")), Integer.class.cast(source.get("code")));
    }

    ClassificationSuggestionDto convertClassificationSuggestion(CompletionSuggestion.Entry.Option option, Language language) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        String label = String.class.cast(Map.class.cast(source.get("labels")).get(language.name()));
        int code = Integer.class.cast(source.get("code"));
        return new ClassificationSuggestionDto(label, code);
    }

    LocalitySuggestionDto convertLocalitySuggestion(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new LocalitySuggestionDto()
            .city(String.class.cast(source.get("city")))
            .communalCode(Integer.class.cast(source.get("communalCode")))
            .cantonCode(String.class.cast(source.get("cantonCode")));
    }

    CantonSuggestionDto convertCantonSuggestion(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new CantonSuggestionDto().code(String.class.cast(source.get("code"))).name(String.class.cast(source.get("name")));
    }

    Suggestions extractSuggestions(Labels labels) {
        return new Suggestions()
            .de(extractSuggestionList(labels.getDe()))
            .fr(extractSuggestionList(labels.getFr()))
            .it(extractSuggestionList(labels.getIt()))
            .en(extractSuggestionList(labels.getEn()));
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
