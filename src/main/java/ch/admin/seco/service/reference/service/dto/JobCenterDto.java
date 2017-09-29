package ch.admin.seco.service.reference.service.dto;

import java.util.UUID;

public class JobCenterDto {

    private UUID id;

    private String code;

    private String email;

    private String phone;

    private String fax;

    private boolean showContactDetailsToPublic;

    private AddressDto address;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JobCenterDto id(UUID id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JobCenterDto code(String code) {
        this.code = code;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JobCenterDto email(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public JobCenterDto phone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public JobCenterDto fax(String fax) {
        this.fax = fax;
        return this;
    }

    public boolean isShowContactDetailsToPublic() {
        return showContactDetailsToPublic;
    }

    public void setShowContactDetailsToPublic(boolean showContactDetailsToPublic) {
        this.showContactDetailsToPublic = showContactDetailsToPublic;
    }

    public JobCenterDto showContactDetailsToPublic(boolean showContactDetailsToPublic) {
        this.showContactDetailsToPublic = showContactDetailsToPublic;
        return this;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public JobCenterDto address(AddressDto address) {
        this.address = address;
        return this;
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
