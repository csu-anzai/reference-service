package ch.admin.seco.service.reference.domain.search;

import java.util.Set;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import ch.admin.seco.service.reference.domain.OccupationSynonym;

/**
 * A Occupation2.
 */
@Document(indexName = "occupations", type = "occupation")
@Mapping(mappingPath = "config/elasticsearch/mappings/occupation.json")
public class OccupationSynonymSuggestion extends OccupationSynonym<OccupationSynonymSuggestion> {

    private Set<String> occupationSuggestions;

    public Set<String> getOccupationSuggestions() {
        return occupationSuggestions;
    }

    public void setOccupationSuggestions(Set<String> occupationSuggestions) {
        this.occupationSuggestions = occupationSuggestions;
    }

    public OccupationSynonymSuggestion occupationSuggestions(Set<String> occupationSuggestions) {
        this.occupationSuggestions = occupationSuggestions;
        return this;
    }

    @Override
    public String toString() {
        return "OccupationSynonymSuggestion{" +
            "occupationSuggestions=" + occupationSuggestions +
            "} " + super.toString();
    }
}
