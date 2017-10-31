package ch.admin.seco.service.reference.service.dto.mapper;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

import org.springframework.stereotype.Component;

import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.service.dto.OccupationDto;

@Component
public class OccupationDtoMapper {

    public OccupationDtoMapper() {
    }

    public OccupationDto toOccupationDto(Occupation occupation, Language language) {
        return new OccupationDto()
            .id(occupation.getId())
            .code(occupation.getCode())
            .classificationCode(occupation.getClassificationCode())
            .labels(getMaleLabel(language, occupation), getFemaleLabel(language, occupation));
    }

    private String getMaleLabel(Language language, Occupation occupation) {
        return hasMaleLabel(language, occupation) ?
            occupation.getMaleLabels().get(language) : occupation.getMaleLabels().get(Language.de);
    }

    private String getFemaleLabel(Language language, Occupation occupation) {
        // only use the backup language for female labels if the male label is not set,
        // this is mainly used for English language
        return hasMaleLabel(language, occupation) ?
            occupation.getFemaleLabels().get(language) : occupation.getFemaleLabels().get(Language.de);
    }

    private boolean hasMaleLabel(Language language, Occupation occupation) {
        return nonNull(occupation.getMaleLabels()) && hasText(occupation.getMaleLabels().get(language));
    }

}
