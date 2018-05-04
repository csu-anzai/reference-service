package ch.admin.seco.service.reference.service.dto;

import org.springframework.data.domain.Pageable;

import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;

public class OccupationLabelSearchRequestDto {

    final private ProfessionCodeType codeType;

    final private String prefix;

    final private Pageable pageable;

    public OccupationLabelSearchRequestDto(ProfessionCodeType codeType, String prefix, Pageable pageable) {
        this.codeType = codeType;
        this.prefix = prefix;
        this.pageable = pageable;
    }

    public ProfessionCodeType getCodeType() {
        return codeType;
    }

    public String getPrefix() {
        return prefix;
    }

    public Pageable getPageable() {
        return pageable;
    }
}
