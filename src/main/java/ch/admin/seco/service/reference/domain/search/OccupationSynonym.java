package ch.admin.seco.service.reference.domain.search;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Id;

import ch.admin.seco.service.reference.domain.Language;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

/**
 * A Occupation2.
 */
@Document(indexName = "occupation", type = "occupations")
@Mapping(mappingPath = "config/elasticsearch/mappings/occupation.json")
public class OccupationSynonym implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;

    private int code;

    private Language language;

    private String occupation;

    private Set<String> occupationSuggestions;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OccupationSynonym id(UUID id) {
        setId(id);
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public OccupationSynonym code(int code) {
        this.code = code;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public OccupationSynonym language(Language language) {
        this.language = language;
        return this;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public OccupationSynonym occupation(String occupation) {
        this.occupation = occupation;
        return this;
    }

    public Set<String> getOccupationSuggestions() {
        return occupationSuggestions;
    }

    public void setOccupationSuggestions(Set<String> occupationSuggestions) {
        this.occupationSuggestions = occupationSuggestions;
    }

    public OccupationSynonym occupationSuggestions(Set<String> occupationSuggestions) {
        this.occupationSuggestions = occupationSuggestions;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OccupationSynonym occupation = (OccupationSynonym) o;
        if (occupation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), occupation.getId());
    }

    @Override
    public String toString() {
        return "OccupationSynonym{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", language='" + getLanguage() + "'" +
            ", occupation='" + getOccupation() + "'" +
            "}";
    }
}
