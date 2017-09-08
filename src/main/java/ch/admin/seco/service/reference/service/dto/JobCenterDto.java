package ch.admin.seco.service.reference.service.dto;

import java.util.UUID;

public class JobCenterDto {

    private UUID id;

    private String code;

    private String email;

    private String phone;

    private String fax;

    private AddressDto address;

    public UUID getId() {
        return id;
    }

    public JobCenterDto id(UUID id) {
        this.id = id;
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public JobCenterDto code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public JobCenterDto email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public JobCenterDto phone(String phone) {
        this.phone = phone;
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public JobCenterDto fax(String fax) {
        this.fax = fax;
        return this;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public AddressDto getAddress() {
        return address;
    }

    public JobCenterDto address(AddressDto address) {
        this.address = address;
        return this;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "JobCenterDto{" +
            "code='" + code + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", fax='" + fax + '\'' +
            ", address=" + address +
            '}';
    }
}
