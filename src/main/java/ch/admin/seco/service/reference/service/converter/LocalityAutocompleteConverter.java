package ch.admin.seco.service.reference.service.converter;

import org.elasticsearch.action.search.SearchResponse;

import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;

public interface LocalityAutocompleteConverter {

    LocalityAutocompleteDto convert(SearchResponse searchResponse, int resultSize);
}
