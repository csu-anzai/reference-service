package ch.admin.seco.service.reference.domain.search;

import java.util.Set;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import ch.admin.seco.service.reference.domain.Canton;

@Document(indexName = "localities", type = "canton")
@Mapping(mappingPath = "config/elasticsearch/mappings/canton.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class CantonSuggestion extends Canton<CantonSuggestion> {

    private Set<String> cantonSuggestions;

    public Set<String> getCantonSuggestions() {
        return cantonSuggestions;
    }

    public void setCantonSuggestions(Set<String> cantonSuggestions) {
        this.cantonSuggestions = cantonSuggestions;
    }

    public CantonSuggestion cantonSuggestions(Set<String> cantonSuggestions) {
        this.cantonSuggestions = cantonSuggestions;
        return this;
    }

    @Override
    public String toString() {
        return "CantonSuggestion{" +
            "cantonSuggestions=" + cantonSuggestions +
            "} " + super.toString();
    }
}
