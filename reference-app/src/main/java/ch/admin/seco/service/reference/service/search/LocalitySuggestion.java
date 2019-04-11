package ch.admin.seco.service.reference.service.search;

import ch.admin.seco.service.reference.domain.Locality;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Set;

@Document(indexName = "localities", type = "locality")
@Mapping(mappingPath = "config/elasticsearch/mappings/locality.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class LocalitySuggestion extends Locality<LocalitySuggestion> {

    private Set<String> citySuggestions;

    public Set<String> getCitySuggestions() {
        return citySuggestions;
    }

    public void setCitySuggestions(Set<String> citySuggestions) {
        this.citySuggestions = citySuggestions;
    }

    public LocalitySuggestion citySuggestions(Set<String> citySuggestions) {
        this.citySuggestions = citySuggestions;
        return this;
    }

    @Override
    public String toString() {
        return "LocalitySuggestion{" +
            "citySuggestions=" + citySuggestions +
            "} " + super.toString();
    }
}
