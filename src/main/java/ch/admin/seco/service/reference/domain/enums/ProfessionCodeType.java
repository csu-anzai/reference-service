package ch.admin.seco.service.reference.domain.enums;

import java.util.Collection;

public enum ProfessionCodeType {
    AVAM,
    BFS,
    X28,
    SBN3,
    SBN5;

    public static boolean hasClassificationType(Collection<ProfessionCodeType> types) {
        return types.stream().anyMatch(ProfessionCodeType::isClassification);
    }

    public static boolean isClassification(ProfessionCodeType codeType) {
        return SBN3.equals(codeType) || SBN5.equals(codeType);
    }
}
