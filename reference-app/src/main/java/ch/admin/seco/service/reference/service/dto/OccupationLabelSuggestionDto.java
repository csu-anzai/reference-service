package ch.admin.seco.service.reference.service.dto;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;

public class OccupationLabelSuggestionDto {
    private UUID id;

    private String code;

    private ProfessionCodeType type;

    private Language language;

    private String classifier;

    private String label;

    private Map<ProfessionCodeType, String> mappings;

    public UUID getId() {
        return id;
    }

    public OccupationLabelSuggestionDto id(UUID id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OccupationLabelSuggestionDto code(String code) {
        this.code = code;
        return this;
    }

    public ProfessionCodeType getType() {
        return type;
    }

    public void setType(ProfessionCodeType type) {
        this.type = type;
    }

    public OccupationLabelSuggestionDto type(ProfessionCodeType type) {
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

    public Map<ProfessionCodeType, String> getMappings() {
        return mappings;
    }

    public void setMappings(Map<ProfessionCodeType, String> mappings) {
        this.mappings = mappings;
    }

    public OccupationLabelSuggestionDto mappings(Map<ProfessionCodeType, String> mappings) {
        this.mappings = mappings;
        return this;
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
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
        return Objects.equals(getId(), that.getId());
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
