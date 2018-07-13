package ch.admin.seco.service.reference.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import ch.admin.seco.service.reference.domain.valueobject.Address;

@Entity
@Table(name = "job_center")
public class JobCenter extends AbstractAuditingEntity implements Serializable {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @Column(name = "code", length = 5, nullable = false)
    @NotNull
    private String code;

    @Column(name = "email", nullable = false)
    @Email
    private String email;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Column(name = "fax", length = 20)
    private String fax;

    @Column(name = "show_contact_details_to_public")
    private boolean showContactDetailsToPublic;

    @Valid
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_center_addresses", joinColumns = @JoinColumn(name = "job_center_id"))
    private Set<Address> addresses;

    @ElementCollection
    @CollectionTable(name = "job_center_postal_codes",
        joinColumns = @JoinColumn(name = "job_center_code", referencedColumnName = "code"))
    @Column(name = "postal_code")
    private Set<String> postalCodes = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JobCenter id(UUID id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JobCenter code(String code) {
        this.code = code;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JobCenter email(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public JobCenter phone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public JobCenter fax(String fax) {
        this.fax = fax;
        return this;
    }

    public boolean isShowContactDetailsToPublic() {
        return showContactDetailsToPublic;
    }

    public void setShowContactDetailsToPublic(boolean showContactDetailsToPublic) {
        this.showContactDetailsToPublic = showContactDetailsToPublic;
    }

    public JobCenter showContactDetailsToPublic(boolean showContactDetailsToPublic) {
        this.showContactDetailsToPublic = showContactDetailsToPublic;
        return this;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public JobCenter addresses(Set<Address> addresses) {
        this.addresses = addresses;
        return this;
    }

    public Set<String> getPostalCodes() {
        return postalCodes;
    }

    private void setPostalCodes(Set<String> postalCodes) {
        this.postalCodes.clear();
        this.postalCodes.addAll(postalCodes);
    }

    public JobCenter postalCodes(Set<String> postalCodes) {
        this.setPostalCodes(postalCodes);
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

        JobCenter jobCenter = (JobCenter) o;

        return id.equals(jobCenter.id);
    }

    @Override
    public String toString() {
        return "JobCenter{" +
            "id=" + id +
            ", code='" + code + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", fax='" + fax + '\'' +
            ", showContactDetailsToPublic=" + showContactDetailsToPublic +
            ", addresses=" + addresses +
            ", postalCodes=" + postalCodes +
            "}";
    }
}
