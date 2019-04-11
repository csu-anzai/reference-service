package ch.admin.seco.service.reference.service.dto.mapper;

import org.springframework.stereotype.Component;

import ch.admin.seco.service.reference.domain.valueobject.Address;
import ch.admin.seco.service.reference.service.dto.AddressDto;

@Component
public class AddressDtoMapper {

    public AddressDto addressToDto(Address address) {
        return new AddressDto()
            .name(address.getName())
            .city(address.getCity())
            .street(address.getStreet())
            .houseNumber(address.getHouseNumber())
            .zipCode(address.getZipCode());
    }
}
