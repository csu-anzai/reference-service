package ch.admin.seco.service.reference.domain;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import ch.admin.seco.service.reference.domain.valueobject.Address;

@Entity
@Table(name = "job_center")
public class JobCenter implements Serializable {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @Column(name = "code")
    @NotNull
    private String code;

    @Column(name = "email")
    @NotNull
    private String email;

    @Column(name = "phone")
    @NotNull
    private String phone;

    @Column(name = "fax")
    private String fax;

    @Valid
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_center_addresses", joinColumns = @JoinColumn(name = "job_center_id"))
    private Set<Address> addresses;

    public UUID getId() {
        return id;
    }

    public JobCenter id(UUID id) {
        this.id = id;
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public JobCenter code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public JobCenter email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public JobCenter phone(String phone) {
        this.phone = phone;
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public JobCenter fax(String fax) {
        this.fax = fax;
        return this;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public JobCenter addresses(Set<Address> addresses) {
        this.addresses = addresses;
        return this;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
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
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
