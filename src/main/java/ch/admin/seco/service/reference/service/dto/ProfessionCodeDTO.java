package ch.admin.seco.service.reference.service.dto;

import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;

public class ProfessionCodeDTO {

    private String code;

    private ProfessionCodeType codeType;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ProfessionCodeDTO code(String code) {
        this.code = code;
        return this;
    }

    public ProfessionCodeType getCodeType() {
        return codeType;
    }

    public void setCodeType(ProfessionCodeType codeType) {
        this.codeType = codeType;
    }

    public ProfessionCodeDTO codeType(ProfessionCodeType codeType) {
        this.codeType = codeType;
        return this;
    }

    @Override
    public String toString() {
        return "ProfessionCodeDTO{" +
            "code='" + code + '\'' +
            ", codeType='" + codeType + '\'' +
            '}';
    }
}
