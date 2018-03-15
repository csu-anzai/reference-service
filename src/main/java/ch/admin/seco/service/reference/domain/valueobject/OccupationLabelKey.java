package ch.admin.seco.service.reference.domain.valueobject;

import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;

public class OccupationLabelKey {

    private final ProfessionCodeType type;
    private final String code;
    private final Language language;

    public OccupationLabelKey(ProfessionCodeType type, String code, Language language) {
        this.type = type;
        this.code = code;
        this.language = language;
    }

    public ProfessionCodeType getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public Language getLanguage() {
        return language;
    }
}
