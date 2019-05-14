package ch.admin.seco.service.reference.service.dto;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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

    public OccupationLabelSuggestionDto setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public OccupationLabelSuggestionDto setCode(String code) {
        this.code = code;
        return this;
    }

    public ProfessionCodeType getType() {
        return type;
    }

    public OccupationLabelSuggestionDto setType(ProfessionCodeType type) {
        this.type = type;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public OccupationLabelSuggestionDto setLanguage(Language language) {
        this.language = language;
        return this;
    }

    public String getClassifier() {
        return classifier;
    }

    public OccupationLabelSuggestionDto setClassifier(String classifier) {
        this.classifier = classifier;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public OccupationLabelSuggestionDto setLabel(String label) {
        this.label = label;
        return this;
    }

    public Map<ProfessionCodeType, String> getMappings() {
        return mappings;
    }

    public OccupationLabelSuggestionDto setMappings(Map<ProfessionCodeType, String> mappings) {
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
