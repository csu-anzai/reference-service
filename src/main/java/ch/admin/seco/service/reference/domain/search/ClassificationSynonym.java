package ch.admin.seco.service.reference.domain.search;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Id;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import ch.admin.seco.service.reference.domain.Language;

@Document(indexName = "occupation", type = "classifications")
@Mapping(mappingPath = "config/elasticsearch/mappings/classification.json")
public class ClassificationSynonym {

    @Id
    private UUID id;

    private int code;

    private Language language;

    private String classification;

    private Set<String> classificationSuggestions;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ClassificationSynonym id(UUID id) {
        setId(id);
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ClassificationSynonym code(int code) {
        this.code = code;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public ClassificationSynonym language(Language language) {
        this.language = language;
        return this;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public ClassificationSynonym classification(String classification) {
        this.classification = classification;
        return this;
    }

    public Set<String> getClassificationSuggestions() {
        return classificationSuggestions;
    }

    public void setClassificationSuggestions(Set<String> classificationSuggestions) {
        this.classificationSuggestions = classificationSuggestions;
    }

    public ClassificationSynonym classificationSuggestions(Set<String> classificationSuggestions) {
        this.classificationSuggestions = classificationSuggestions;
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
        ClassificationSynonym classification = (ClassificationSynonym) o;
        if (classification.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), classification.getId());
    }

    @Override
    public String toString() {
        return "ClassificationSynonym{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", language='" + getLanguage() + "'" +
            ", classification='" + getClassification() + "'" +
            "}";
    }
}
