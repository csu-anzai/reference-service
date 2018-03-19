package ch.admin.seco.service.reference.domain.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

        return cantonCodes.stream().reduce("",
                (accumulator, code) -> accumulator.concat(String.format(",%s", code)));
    }

    @Override
    public Set<String> convertToEntityAttribute(String value) {
        if (StringUtils.isEmpty(value)) {
            return Collections.emptySet();
        }

        final String[] cantonCodes = value.split(",");
        return new HashSet<>(Arrays.asList(cantonCodes));
    }
}
