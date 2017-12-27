package ch.admin.seco.service.reference.service.dto;

import java.util.Map;

import ch.admin.seco.service.reference.domain.enums.Language;

public class OccupationLabelDto {
    private int code;

    private String type;

    private Language language;

    private Map<String, String> labels;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public OccupationLabelDto code(int code) {
        this.code = code;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OccupationLabelDto type(String type) {
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
