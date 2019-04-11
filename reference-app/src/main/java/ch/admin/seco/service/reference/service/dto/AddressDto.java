package ch.admin.seco.service.reference.service.dto;

public class AddressDto {

    private String name;

    private String city;

    private String street;

    private String houseNumber;

    private String zipCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressDto name(String name) {
        this.name = name;
        return this;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public AddressDto city(String city) {
        this.city = city;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public AddressDto street(String street) {
        this.street = street;
        return this;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public AddressDto houseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public AddressDto zipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    @Override
    public String toString() {
        return "AddressDto{" +
            "name='" + name + '\'' +
            ", city='" + city + '\'' +
            ", street='" + street + '\'' +
            ", houseNumber='" + houseNumber + '\'' +
            ", zipCode=" + zipCode +
            '}';
    }
}
