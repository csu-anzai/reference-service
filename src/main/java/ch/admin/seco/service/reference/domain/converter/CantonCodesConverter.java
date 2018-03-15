package ch.admin.seco.service.reference.domain.converter;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Converter
public class CantonCodesConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> cantonCodes) {
        if (CollectionUtils.isEmpty(cantonCodes)) {
            return null;
        }

        return cantonCodes.stream()
            .map(String::trim)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.joining(","));
    }

    @Override
    public Set<String> convertToEntityAttribute(String value) {
        if (StringUtils.isEmpty(value)) {
            return Collections.emptySet();
        }

        return Stream.of(value.split(","))
            .map(String::trim)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toSet());
    }
}
