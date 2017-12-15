package ch.admin.seco.service.reference.service.factory;

import ch.admin.seco.service.reference.service.converter.DefaultLocalitySuggestionConverter;
import ch.admin.seco.service.reference.service.converter.DistinctLocalitySuggestionConverter;
import ch.admin.seco.service.reference.service.converter.LocalitySuggestionConverter;
import ch.admin.seco.service.reference.service.dto.LocalitySearchDto;

public final class LocalitySuggestionConverterFactory {

    private LocalitySuggestionConverterFactory() {
    }

    public static LocalitySuggestionConverter getConverter(LocalitySearchDto searchRequest) {
        return searchRequest.isDistinctLocalities()
            ? new DistinctLocalitySuggestionConverter()
            : new DefaultLocalitySuggestionConverter();
    }
}
