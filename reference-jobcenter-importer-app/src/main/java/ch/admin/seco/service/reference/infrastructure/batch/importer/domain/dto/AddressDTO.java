package ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto;

import javax.persistence.Embeddable;

@Embeddable
public class AddressDTO {

    private String name;

    private String city;

    private String street;

    private String houseNumber;

    private String zipCode;

    private String language;

    public String getName() {
        return name;
    }

    public AddressDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCity() {
        return city;
    }

    public AddressDTO setCity(String city) {
        this.city = city;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public AddressDTO setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public AddressDTO setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public String getZipCode() {
        return zipCode;
    }

    public AddressDTO setZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public AddressDTO setLanguage(String language) {
        this.language = language;
        return this;
    }

    @Override
    public String toString() {
        return "AddressDTO{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
