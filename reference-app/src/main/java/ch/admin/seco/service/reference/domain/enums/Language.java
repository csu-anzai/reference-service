package ch.admin.seco.service.reference.domain.enums;

public enum Language {
    de, fr, it, en;

    public static Language safeValueOf(String language) {
        try {
            return valueOf(language);
        } catch (Exception e) {
            return de;
        }
    }
}
