package ch.admin.seco.service.reference.domain.enums;

public enum Language {
    de, fr, it, en;

    public static Language safeValueOf(String langugage) {
        try {
            return valueOf(langugage);
        } catch (Exception e) {
            return de;
        }
    }
}
