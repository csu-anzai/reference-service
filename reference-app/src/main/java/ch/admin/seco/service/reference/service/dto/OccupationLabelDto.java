package ch.admin.seco.service.reference.service.dto;

import java.util.Map;

import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;

public class OccupationLabelDto {
    private String code;

    private ProfessionCodeType type;

    private Language language;

    private Map<String, String> labels;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OccupationLabelDto code(String code) {
        this.code = code;
        return this;
    }

    public ProfessionCodeType getType() {
        return type;
    }

    public void setType(ProfessionCodeType type) {
        this.type = type;
    }

    public OccupationLabelDto type(ProfessionCodeType type) {
        this.type = type;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public OccupationLabelDto language(Language language) {
        this.language = language;
        return this;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public OccupationLabelDto labels(Map<String, String> labels) {
        this.labels = labels;
        return this;
    }

}
