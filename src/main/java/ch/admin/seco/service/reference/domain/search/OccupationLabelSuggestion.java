package ch.admin.seco.service.reference.domain.search;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import ch.admin.seco.service.reference.domain.OccupationLabel;

/**
 * A Occupation2.
 */
@Document(indexName = "occupationlabels", type = "occupation")
@Mapping(mappingPath = "config/elasticsearch/mappings/occupation-label.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class OccupationLabelSuggestion extends OccupationLabel<OccupationLabelSuggestion> {

    private String contextKey;

    private Set<String> occupationSuggestions;

    private Map<String, Integer> mappings;

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
