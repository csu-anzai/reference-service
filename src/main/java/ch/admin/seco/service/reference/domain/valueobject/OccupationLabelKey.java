package ch.admin.seco.service.reference.domain.valueobject;

import ch.admin.seco.service.reference.domain.enums.Language;

public class OccupationLabelKey {

    private final String type;
    private final int code;
    private final Language language;

    public OccupationLabelKey(String type, int code, Language language) {
        this.type = type;
        this.code = code;
        this.language = language;
    }

    public String getType() {
        return type;
    }

    public int getCode() {
        return code;
    }

    public Language getLanguage() {
        return language;
    }
}
