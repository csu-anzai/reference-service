package ch.admin.seco.service.reference.domain.valueobject;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import ch.admin.seco.service.reference.domain.enums.Language;

@Embeddable
public class Address {

    @Column(name = "name", length = 100, nullable = false)
    @NotNull
    private String name;

    @Column(name = "city", length = 100, nullable = false)
    @NotNull
    private String city;

    @Column(name = "street", length = 100, nullable = true)
    private String street;

    @Column(name = "house_number", length = 7, nullable = true)
    private String houseNumber;

    @Column(name = "zip_code", length = 4, nullable = false)
    @NotNull
    private String zipCode;

    @Column(name = "language", length = 2, nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private Language language;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address name(String name) {
        this.name = name;
        return this;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Address city(String city) {
        this.city = city;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Address street(String street) {
        this.street = street;
        return this;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public Address houseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Address zipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Address language(Language language) {
        this.language = language;
        return this;
    }

    @Override
    public String toString() {
        return "Address{" +
            "name='" + name + '\'' +
            ", city='" + city + '\'' +
            ", street='" + street + '\'' +
            ", houseNumber='" + houseNumber + '\'' +
            ", zipCode='" + zipCode + '\'' +
            ", language=" + language +
            '}';
    }
}
