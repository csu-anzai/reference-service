package ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class JobCenterDTO implements Serializable {

    private UUID id;

    private String code;

    private String email;

    private String phone;

    private String fax;

    private boolean showContactDetailsToPublic;

    private Set<AddressDTO> addresses;

    public UUID getId() {
        return id;
    }

    public JobCenterDTO setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public JobCenterDTO setCode(String code) {
        this.code = code;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public JobCenterDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public JobCenterDTO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getFax() {
        return fax;
    }

    public JobCenterDTO setFax(String fax) {
        this.fax = fax;
        return this;
    }

    public boolean isShowContactDetailsToPublic() {
        return showContactDetailsToPublic;
    }

    public JobCenterDTO setShowContactDetailsToPublic(boolean showContactDetailsToPublic) {
        this.showContactDetailsToPublic = showContactDetailsToPublic;
        return this;
    }

    public Set<AddressDTO> getAddresses() {
        return addresses;
    }

    public JobCenterDTO setAddresses(Set<AddressDTO> addresses) {
        this.addresses = addresses;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JobCenterDTO jobCenter = (JobCenterDTO) o;

        return id.equals(jobCenter.id);
    }

    @Override
    public String toString() {
        return "JobCenterDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", fax='" + fax + '\'' +
                ", showContactDetailsToPublic=" + showContactDetailsToPublic +
                ", addresses=" + addresses +
                '}';
    }
}
