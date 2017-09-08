package ch.admin.seco.service.reference.service.dto;

public class AddressDto {

    private String name;

    private String city;

    private String street;

    private String houseNumber;

    private int zipCode;

    public String getName() {
        return name;
    }

    public AddressDto name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public AddressDto city(String city) {
        this.city = city;
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public AddressDto street(String street) {
        this.street = street;
        return this;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public AddressDto houseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public int getZipCode() {
        return zipCode;
    }

    public AddressDto zipCode(int zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
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
