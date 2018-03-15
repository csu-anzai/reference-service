package ch.admin.seco.service.reference.service.impl;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.StringUtils.hasText;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.OccupationLabel;
import ch.admin.seco.service.reference.domain.OccupationLabelMapping;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;
import ch.admin.seco.service.reference.repository.OccupationLabelMappingRepository;
import ch.admin.seco.service.reference.repository.OccupationLabelRepository;
import ch.admin.seco.service.reference.service.OccupationLabelService;
import ch.admin.seco.service.reference.service.dto.OccupationLabelAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.ProfessionCodeDTO;

/**
 * Service Implementation for managing Occupation.
 */
@Service
@Transactional
public class OccupationLabelServiceImpl implements OccupationLabelService {

    private final Logger log = LoggerFactory.getLogger(OccupationLabelServiceImpl.class);
    private final OccupationLabelMappingRepository occupationMappingRepository;
    private final OccupationLabelRepository occupationLabelRepository;
    private final OccupationLabelSuggestionImpl occupationSuggestionImpl;
    private final GenderNeutralOccupationLabelGenerator labelGenerator;

    public OccupationLabelServiceImpl(OccupationLabelMappingRepository occupationMappingRepository,
        OccupationLabelRepository occupationLabelRepository,
        OccupationLabelSuggestionImpl occupationSuggestion,
        GenderNeutralOccupationLabelGenerator labelGenerator) {

        this.occupationMappingRepository = occupationMappingRepository;
        this.occupationLabelRepository = occupationLabelRepository;
        this.occupationSuggestionImpl = occupationSuggestion;
        this.labelGenerator = labelGenerator;
    }

    @Override
    public OccupationLabel save(OccupationLabel occupationLabel) {
        return this.occupationLabelRepository.save(occupationLabel);
    }

    @Override
    public Optional<OccupationLabelMapping> findOneOccupationMapping(ProfessionCodeDTO professionCode) {
        log.debug("Request to get OccupationLabelMapping : {}", professionCode);
        final String code = professionCode.getCode();
        switch (professionCode.getCodeType()) {
            case X28:
                return occupationMappingRepository.findOneByX28Code(code);
            case AVAM:
                return occupationMappingRepository.findOneByAvamCode(code);
            case BFS:
                return occupationMappingRepository.findByBfsCode(code).stream().findFirst();
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    @Override
    public OccupationLabelAutocompleteDto suggest(String prefix, Language language, Collection<ProfessionCodeType> types, int resultSize) {
        return occupationSuggestionImpl.suggest(prefix, language, types, resultSize);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getOccupationLabels(ProfessionCodeDTO professionCode, Language language) {
        log.debug("Request to get OccupationLabels : professionCode:{}, language:{}", professionCode, language);
        final ProfessionCodeType codeType = professionCode.getCodeType();
        List<OccupationLabel> occupationLabels = occupationLabelRepository
            .findByCodeAndTypeAndLanguage(professionCode.getCode(), codeType, language);
        if (occupationLabels.isEmpty()) {
            occupationLabels = occupationLabelRepository
                .findByCodeAndTypeAndLanguage(professionCode.getCode(), codeType, Language.de);
        }
        Map<String, String> labels = occupationLabels.stream()
            .collect(toMap(item -> hasText(item.getClassifier()) ? item.getClassifier() : "default", OccupationLabel::getLabel));
        if (!labels.containsKey("default")) {
            labels.put("default", labelGenerator.generate(labels.get("m"), labels.get("f")));
        }
        return labels;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Map<String, String>> getOccupationLabels(ProfessionCodeDTO professionCode, Language language, String classifier) {
        log.debug("Request to get OccupationLabels : professionCode:{}, classifier:{}, language:{}", professionCode, classifier, language);
        return ofNullable(getBestMatchingOccupationLabel(professionCode, language, classifier))
            .map(item -> ImmutableMap.of("label", item.getLabel()));
    }

    private OccupationLabel getBestMatchingOccupationLabel(ProfessionCodeDTO professionCode, Language language, String classifier) {
        final ProfessionCodeType codeType = professionCode.getCodeType();
        return occupationLabelRepository.findOneByCodeAndTypeAndLanguageAndClassifier(professionCode.getCode(),
            codeType, language, classifier)
            .orElseGet(() ->
                // if the requested language was not found try to get another language
                occupationLabelRepository.findByCodeAndTypeAndClassifier(professionCode.getCode(), codeType, classifier)
                    .stream()
                    .reduce((bestMatch, current) -> {
                        if (isNull(bestMatch)) {
                            // select the first entry as default
                            return current;
                        } else if (Language.de.equals(current.getLanguage())) {
                            // German labels have priority
                            return current;
                        }
                        return bestMatch;
                    })
                    .orElse(null)
            );
    }
}
