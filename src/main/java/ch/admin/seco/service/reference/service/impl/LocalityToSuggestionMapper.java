package ch.admin.seco.service.reference.service.impl;

import static java.util.Objects.isNull;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import ch.admin.seco.service.reference.domain.Canton;
import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.search.CantonSuggestion;
import ch.admin.seco.service.reference.domain.search.LocalitySuggestion;

@Component
class LocalityToSuggestionMapper {

    private Map<String, String[]> synonymsMap;

    Locality fromSynonym(LocalitySuggestion localitySuggestion) {
        return new Locality()
                .id(localitySuggestion.getId())
                .city(localitySuggestion.getCity())
                .zipCode(localitySuggestion.getZipCode())
                .communalCode(localitySuggestion.getCommunalCode())
                .cantonCode(localitySuggestion.getCantonCode())
                .regionCode(localitySuggestion.getRegionCode())
                .geoPoint(localitySuggestion.getGeoPoint());
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

    private Set<String> extractSuggestions(String term) {
        if (isNull(term)) {
            return Collections.emptySet();
        }
        Set<String> suggestions = new HashSet<>();
        suggestions.add(term);
        CollectionUtils.mergeArrayIntoCollection(synonymsMap.get(term.toLowerCase()), suggestions);
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

    @PostConstruct
    private void init() {
        this.synonymsMap = loadLocalitySynonyms();
    }

    private Map<String, String[]> loadLocalitySynonyms() {
        ClassPathResource file = new ClassPathResource("config/elasticsearch/settings/locality-synonyms.txt");
        Map<String, String[]> synonymsMap = new HashMap<>();
        try {
            Files.lines(file.getFile().toPath())
                    .filter(line -> !line.startsWith("#"))
                    .map(line -> line.split(","))
                    .filter(tokens -> tokens.length > 1)
                    .forEach(tokens -> {
                        for (int i = 0; i < tokens.length; i++) {
                            tokens[i] = tokens[i].trim();
                        }

                        Stream.of(tokens)
                                .map(String::toLowerCase)
                                .collect(Collectors.toMap(String::toLowerCase, token -> tokens, (a, b) -> a, () -> synonymsMap));
                    });
        } catch (IOException e) {
            LoggerFactory.getLogger(this.getClass())
                    .error("Failed to load locality-synonyms.txt", e);
        }
        return synonymsMap;
    }
}
