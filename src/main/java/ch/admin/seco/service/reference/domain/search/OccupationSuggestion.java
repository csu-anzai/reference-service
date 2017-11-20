package ch.admin.seco.service.reference.domain.search;

import java.util.Set;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import ch.admin.seco.service.reference.domain.OccupationSynonym;

/**
 * A Occupation2.
 */
@Document(indexName = "occupations", type = "occupation")
@Mapping(mappingPath = "config/elasticsearch/mappings/occupation.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class OccupationSuggestion extends OccupationSynonym<OccupationSuggestion> {

    private Set<String> occupationSuggestions;

    private boolean isSynonym;

    public Set<String> getOccupationSuggestions() {
        return occupationSuggestions;
    }

    public void setOccupationSuggestions(Set<String> occupationSuggestions) {
        this.occupationSuggestions = occupationSuggestions;
    }

    public OccupationSuggestion occupationSuggestions(Set<String> occupationSuggestions) {
        this.occupationSuggestions = occupationSuggestions;
        return this;
    }

    public boolean isSynonym() {
        return isSynonym;
    }

    public void setSynonym(boolean synonym) {
        isSynonym = synonym;
    }

    public OccupationSuggestion synonym(boolean synonym) {
        this.isSynonym = synonym;
        return this;
    }

    public String getCombinedContext() {
        return this.getLanguage() + "_" + isSynonym;
    }

    @Override
    public String toString() {
        return "OccupationSynonymSuggestion{" +
            "occupationSuggestions=" + occupationSuggestions +
            "synonym=" + isSynonym +
            "} " + super.toString();
    }
}
