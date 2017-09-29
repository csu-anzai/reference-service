package ch.admin.seco.service.reference.service.dto.mapper;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import ch.admin.seco.service.reference.domain.JobCenter;
import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.valueobject.Address;
import ch.admin.seco.service.reference.service.dto.AddressDto;
import ch.admin.seco.service.reference.service.dto.JobCenterDto;

@Component
public class JobCenterMapper {

    private final AddressMapper addressMapper;

    public JobCenterMapper(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    public JobCenterDto jobCenterToDto(JobCenter jobCenter, Language language) {
        return new JobCenterDto()
            .id(jobCenter.getId())
            .code(jobCenter.getCode())
            .email(jobCenter.getEmail())
            .phone(jobCenter.getPhone())
            .fax(jobCenter.getFax())
            .showContactDetailsToPublic(jobCenter.isShowContactDetailsToPublic())
            .address(findAddressDtoByLanguageOrDefault(jobCenter.getAddresses(), language, Language.de));
    }

    private AddressDto findAddressDtoByLanguageOrDefault(Set<Address> addresses, Language language, Language defaultLanguage) {
        return findAddressDtoByLanguage(addresses, language)
            .orElseGet(() -> findAddressDtoByLanguage(addresses, defaultLanguage)
                .orElse(null));
    }

    private Optional<AddressDto> findAddressDtoByLanguage(Set<Address> addresses, Language language) {
        return addresses.stream()
            .filter(address -> address.getLanguage().equals(language))
            .findFirst()
            .map(addressMapper::addressToDto);
    }
}
