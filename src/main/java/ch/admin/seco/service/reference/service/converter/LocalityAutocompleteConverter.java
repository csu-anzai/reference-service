package ch.admin.seco.service.reference.service.converter;

import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;
import org.elasticsearch.action.search.SearchResponse;

public interface LocalityAutocompleteConverter {

    LocalityAutocompleteDto convert(SearchResponse localitiesSearchResponse, SearchResponse cantonSearchResponse, int resultSize);
}
