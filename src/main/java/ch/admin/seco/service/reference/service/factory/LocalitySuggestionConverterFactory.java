package ch.admin.seco.service.reference.service.factory;

import ch.admin.seco.service.reference.service.converter.DefaultLocalitySuggestionConverter;
import ch.admin.seco.service.reference.service.converter.DistinctLocalitySuggestionConverter;
import ch.admin.seco.service.reference.service.converter.LocalitySuggestionConverter;
import ch.admin.seco.service.reference.service.dto.LocalitySearchDTO;

public final class LocalitySuggestionConverterFactory {

    private LocalitySuggestionConverterFactory() {
    }

    public static LocalitySuggestionConverter getConverter(LocalitySearchDTO searchRequest) {
        return searchRequest.isDistinctLocalities()
            ? new DistinctLocalitySuggestionConverter()
            : new DefaultLocalitySuggestionConverter();
    }
}
