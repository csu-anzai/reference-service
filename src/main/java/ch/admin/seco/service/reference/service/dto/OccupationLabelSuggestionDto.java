package ch.admin.seco.service.reference.service.dto;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

import ch.admin.seco.service.reference.domain.enums.Language;

public class OccupationLabelSuggestionDto {
    private UUID id;

    private int code;

    private String type;

    private Language language;

    private String classifier;

    private String label;

    private Map<String, Integer> mappings;

    public UUID getId() {
        return id;
    }

    public OccupationLabelSuggestionDto id(UUID id) {
        this.id = id;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public OccupationLabelSuggestionDto code(int code) {
        this.code = code;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OccupationLabelSuggestionDto type(String type) {
        this.type = type;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public OccupationLabelSuggestionDto language(Language language) {
        this.language = language;
        return this;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public OccupationLabelSuggestionDto classifier(String classifier) {
        this.classifier = classifier;
        return this;
    }

    @Nonnull
    public String getLabel() {
        return label;
    }

    public void setLabel(@Nonnull String label) {
        this.label = label;
    }

    public OccupationLabelSuggestionDto label(String label) {
        this.label = label;
        return this;
    }

    public Map<String, Integer> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, Integer> mappings) {
        this.mappings = mappings;
    }

    public OccupationLabelSuggestionDto mappings(Map<String, Integer> mappings) {
        this.mappings = mappings;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCode(), getType(), getLanguage(), getClassifier(), getLabel(), getMappings());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OccupationLabelSuggestionDto that = (OccupationLabelSuggestionDto) o;
        return getCode() == that.getCode() &&
            Objects.equals(getId(), that.getId()) &&
            Objects.equals(getType(), that.getType()) &&
            getLanguage() == that.getLanguage() &&
            Objects.equals(getClassifier(), that.getClassifier()) &&
            Objects.equals(getLabel(), that.getLabel()) &&
            Objects.equals(getMappings(), that.getMappings());
    }

    @Override
    public String toString() {
        return "OccupationDto{" +
            "id=" + id +
            ", code=" + code +
            ", type='" + type + '\'' +
            ", language=" + language +
            ", classifier='" + classifier + '\'' +
            ", label='" + label + '\'' +
            ", mappings=" + mappings +
            '}';
    }
}
