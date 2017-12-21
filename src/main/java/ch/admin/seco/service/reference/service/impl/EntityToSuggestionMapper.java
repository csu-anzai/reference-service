package ch.admin.seco.service.reference.service.impl;

import static java.util.Objects.isNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import ch.admin.seco.service.reference.domain.Canton;
import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.search.CantonSuggestion;
import ch.admin.seco.service.reference.domain.search.LocalitySuggestion;
import ch.admin.seco.service.reference.domain.search.Suggestions;
import ch.admin.seco.service.reference.domain.valueobject.Labels;

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

    private void nextSubTerm(String term, Set<String> suggestions, Pattern pattern) {
        Matcher matcher = pattern.matcher(term);
        if (matcher.find()) {
            term = term.substring(matcher.end());
            suggestions.add(term);
            nextSubTerm(term, suggestions, pattern);
        }
    }
}
