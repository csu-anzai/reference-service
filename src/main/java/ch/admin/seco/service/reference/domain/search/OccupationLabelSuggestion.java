package ch.admin.seco.service.reference.domain.search;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import ch.admin.seco.service.reference.domain.enums.Language;

/**
 * A Occupation2.
 */
@Document(indexName = "occupationlabels", type = "occupation")
@Mapping(mappingPath = "config/elasticsearch/mappings/occupation-label.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class OccupationLabelSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    private int code;

    private String type;

    private String classifier;

    private Language language;

    private String contextKey;

    private String label;

    private Set<String> occupationSuggestions;

    private Map<String, Integer> mappings;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OccupationLabelSuggestion id(UUID id) {
        this.id = id;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public OccupationLabelSuggestion code(int code) {
        this.code = code;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OccupationLabelSuggestion type(String type) {
        this.type = type;
        return this;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public OccupationLabelSuggestion classifier(String classifier) {
        this.classifier = classifier;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public OccupationLabelSuggestion language(Language language) {
        this.language = language;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public OccupationLabelSuggestion label(String label) {
        this.label = label;
        return this;
    }

    public String getContextKey() {
        return contextKey;
    }

    public void setContextKey(String contextKey) {
        this.contextKey = contextKey;
    }

    public OccupationLabelSuggestion contextKey(String contextKey) {
        this.contextKey = contextKey;
        return this;
    }

    public Set<String> getOccupationSuggestions() {
        return occupationSuggestions;
    }

    public void setOccupationSuggestions(Set<String> occupationSuggestions) {
        this.occupationSuggestions = occupationSuggestions;
    }

    public OccupationLabelSuggestion occupationSuggestions(Set<String> occupationSuggestions) {
        this.occupationSuggestions = occupationSuggestions;
        return this;
    }

    public Map<String, Integer> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, Integer> mappings) {
        this.mappings = mappings;
    }

    public OccupationLabelSuggestion mappings(Map<String, Integer> mappings) {
        this.mappings = mappings;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getContextKey(), getOccupationSuggestions(), getMappings());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        OccupationLabelSuggestion that = (OccupationLabelSuggestion) o;
        return Objects.equals(getContextKey(), that.getContextKey()) &&
                Objects.equals(getOccupationSuggestions(), that.getOccupationSuggestions()) &&
                Objects.equals(getMappings(), that.getMappings());
    }

    @Override
    public String toString() {
        return "OccupationSuggestion{" +
                "contextKey='" + contextKey + '\'' +
                ", occupationSuggestions=" + occupationSuggestions +
                ", mappings=" + mappings +
                "} " + super.toString();
    }
}
