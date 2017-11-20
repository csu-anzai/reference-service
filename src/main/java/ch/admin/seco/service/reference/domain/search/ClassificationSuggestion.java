package ch.admin.seco.service.reference.domain.search;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import ch.admin.seco.service.reference.domain.Classification;

@Document(indexName = "occupations", type = "classification")
@Mapping(mappingPath = "config/elasticsearch/mappings/classification.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class ClassificationSuggestion extends Classification<ClassificationSuggestion> {
    private Suggestions classificationSuggestions;

    public Suggestions getClassificationSuggestions() {
        return classificationSuggestions;
    }

    public void setClassificationSuggestions(Suggestions classificationSuggestions) {
        this.classificationSuggestions = classificationSuggestions;
    }

    public ClassificationSuggestion classificationSuggestions(Suggestions classificationSuggestions) {
        this.classificationSuggestions = classificationSuggestions;
        return this;
    }

    @Override
    public String toString() {
        return "ClassificationSuggestion{" +
            "classificationSuggestions=" + classificationSuggestions +
            "} " + super.toString();
    }
}
