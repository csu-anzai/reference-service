package ch.admin.seco.service.reference.service.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.search.suggest.completion.CompletionSuggestion;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ch.admin.seco.service.reference.domain.Canton;
import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.OccupationSynonym;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.search.CantonSuggestion;
import ch.admin.seco.service.reference.domain.search.ClassificationSuggestion;
import ch.admin.seco.service.reference.domain.search.LocalitySuggestion;
import ch.admin.seco.service.reference.domain.search.OccupationSuggestion;
import ch.admin.seco.service.reference.domain.search.Suggestions;
import ch.admin.seco.service.reference.domain.valueobject.Labels;
import ch.admin.seco.service.reference.service.dto.CantonSuggestionDto;
import ch.admin.seco.service.reference.service.dto.ClassificationSuggestionDto;
import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;
import ch.admin.seco.service.reference.service.dto.OccupationSuggestionDto;

@Component
class EntityToSuggestionMapper {

    Locality fromSynonym(LocalitySuggestion localitySynonym) {
        return new Locality()
            .id(localitySynonym.getId())
            .city(localitySynonym.getCity())
            .zipCode(localitySynonym.getZipCode())
            .communalCode(localitySynonym.getCommunalCode())
            .cantonCode(localitySynonym.getCantonCode())
            .regionCode(localitySynonym.getRegionCode())
            .geoPoint(localitySynonym.getGeoPoint());
    }

    OccupationSuggestion toOccupationSuggestion(OccupationSynonym occupationSynonym) {
        return new OccupationSuggestion()
            .id(occupationSynonym.getId())
            .code(occupationSynonym.getCode())
            .language(occupationSynonym.getLanguage())
            .name(occupationSynonym.getName())
            .occupationSuggestions(extractSuggestions(occupationSynonym.getName()))
            .synonym(true);
    }

    Set<OccupationSuggestion> toOccupationSuggestionSet(Occupation occupation) {
        Set<OccupationSuggestion> result = new HashSet<>();
        if (nonNull(occupation.getMaleLabels())) {
            result.add(createOccupationSuggestion(occupation, Language.de, occupation.getMaleLabels().getDe()));
            result.add(createOccupationSuggestion(occupation, Language.fr, occupation.getMaleLabels().getFr()));
            result.add(createOccupationSuggestion(occupation, Language.it, occupation.getMaleLabels().getIt()));
            result.add(createOccupationSuggestion(occupation, Language.en, occupation.getMaleLabels().getEn()));
        }
        if (nonNull(occupation.getFemaleLabels())) {
            result.add(createOccupationSuggestion(occupation, Language.de, occupation.getFemaleLabels().getDe()));
            result.add(createOccupationSuggestion(occupation, Language.fr, occupation.getFemaleLabels().getFr()));
            result.add(createOccupationSuggestion(occupation, Language.it, occupation.getFemaleLabels().getIt()));
            result.add(createOccupationSuggestion(occupation, Language.en, occupation.getFemaleLabels().getEn()));
        }
        result.remove(null);
        return result;
    }

    ClassificationSuggestion toClassificationSuggestion(ch.admin.seco.service.reference.domain.Classification classification) {
        return new ClassificationSuggestion()
            .id(classification.getId())
            .code(classification.getCode())
            .labels(classification.getLabels())
            .classificationSuggestions(extractSuggestions(classification.getLabels()));
    }

    LocalitySuggestionDto convertLocalitySuggestion(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new LocalitySuggestionDto()
            .city(String.class.cast(source.get("city")))
            .communalCode(Integer.class.cast(source.get("communalCode")))
            .cantonCode(String.class.cast(source.get("cantonCode")))
            .regionCode(String.class.cast(source.get("regionCode")))
            .zipCode(String.class.cast(source.get("zipCode")));
    }

    LocalitySuggestion toLocalitySuggestion(Locality locality) {
        return new LocalitySuggestion()
            .id(locality.getId())
            .city(locality.getCity())
            .zipCode(locality.getZipCode())
            .communalCode(locality.getCommunalCode())
            .cantonCode(locality.getCantonCode())
            .regionCode(locality.getRegionCode())
            .geoPoint(locality.getGeoPoint())
            .citySuggestions(extractSuggestions(locality.getCity()));
    }

    CantonSuggestion toCantonSuggestion(Canton canton) {
        return new CantonSuggestion()
            .id(canton.getId())
            .code(canton.getCode())
            .name(canton.getName())
            .cantonSuggestions(extractSuggestions(canton.getName()));
    }

    OccupationSuggestionDto convertOccupationSuggestion(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new OccupationSuggestionDto(
            String.class.cast(source.get("name")),
            Integer.class.cast(source.get("code")));
    }

    ClassificationSuggestionDto toClassificationSuggestion(CompletionSuggestion.Entry.Option option, Language language) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        String label = String.class.cast(Map.class.cast(source.get("labels")).get(language.name()));
        int code = Integer.class.cast(source.get("code"));
        return new ClassificationSuggestionDto(label, code);
    }

    CantonSuggestionDto toCantonSuggestionDto(CompletionSuggestion.Entry.Option option) {
        Map<String, Object> source = option.getHit().getSourceAsMap();
        return new CantonSuggestionDto()
            .code(String.class.cast(source.get("code")))
            .name(String.class.cast(source.get("name")));
    }

    private Suggestions extractSuggestions(Labels labels) {
        return new Suggestions()
            .de(extractSuggestions(labels.getDe()))
            .fr(extractSuggestions(labels.getFr()))
            .it(extractSuggestions(labels.getIt()))
            .en(extractSuggestions(labels.getEn()));
    }

    private Set<String> extractSuggestions(String term) {
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

    private OccupationSuggestion createOccupationSuggestion(Occupation occupation, Language language, String name) {
        if (StringUtils.hasText(name)) {
            return new OccupationSuggestion()
                .id(UUID.randomUUID())
                .code(occupation.getCode())
                .language(language)
                .name(name)
                .occupationSuggestions(extractSuggestions(name))
                .synonym(false);
        }
        return null;
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
