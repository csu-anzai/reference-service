package ch.admin.seco.service.reference.service.factory;

import ch.admin.seco.service.reference.service.converter.DefaultLocalityAutocompleteConverter;
import ch.admin.seco.service.reference.service.converter.DistinctByCityLocalityAutocompleteConverter;
import ch.admin.seco.service.reference.service.converter.LocalityAutocompleteConverter;
import ch.admin.seco.service.reference.service.dto.LocalitySearchDto;

public final class LocalityAutocompleteConverterFactory {

    private LocalityAutocompleteConverterFactory() {
    }

    public static LocalityAutocompleteConverter getConverter(LocalitySearchDto searchRequest) {
        return searchRequest.isDistinctByLocalityCity()
            ? new DistinctByCityLocalityAutocompleteConverter()
            : new DefaultLocalityAutocompleteConverter();
    }
}
