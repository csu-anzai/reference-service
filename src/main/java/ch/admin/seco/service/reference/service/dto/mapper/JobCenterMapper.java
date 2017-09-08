package ch.admin.seco.service.reference.service.dto.mapper;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import org.springframework.stereotype.Component;

import ch.admin.seco.service.reference.domain.JobCenter;
import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.valueobject.Address;
import ch.admin.seco.service.reference.service.dto.AddressDto;
import ch.admin.seco.service.reference.service.dto.JobCenterDto;

@Component
public class JobCenterMapper {

    private final AddressMapper addressMapper;

    private final BiFunction<Set<Address>, Language, Optional<Address>> findAddressByLanguage = (addresses, language) -> addresses.stream()
        .filter(address -> address.getLanguage().equals(language))
        .findFirst();

    public JobCenterMapper(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    public JobCenterDto jobCenterToDto(JobCenter jobCenter, Language language) {
        AddressDto addressDto = findAddressByLanguage(jobCenter.getAddresses(), language)
            .map(addressMapper::addressToDto)
            .orElse(null);

        return new JobCenterDto()
            .id(jobCenter.getId())
            .code(jobCenter.getCode())
            .email(jobCenter.getEmail())
            .phone(jobCenter.getPhone())
            .fax(jobCenter.getFax())
            .address(addressDto);

    }

    private Optional<Address> findAddressByLanguage(Set<Address> addresses, Language language) {
        Optional<Address> address = findAddressByLanguage.apply(addresses, language);
        return address.isPresent()
            ? address
            : findAddressByLanguage.apply(addresses, Language.de);
    }
}
