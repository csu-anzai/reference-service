package ch.admin.seco.service.reference.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

/**
 * A OccupationMapping.
 */
@Entity
@Table(name = "occupation_label_mapping")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OccupationLabelMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NotNull
    @Column(name = "bfs_code", nullable = false)
    private String bfsCode;

    @NotNull
    @Column(name = "avam_code", nullable = false)
    private String avamCode;

    @NotNull
    @Column(name = "sbn3_code", nullable = false)
    private String sbn3Code;

    @NotNull
    @Column(name = "sbn5_code", nullable = false)
    private String sbn5Code;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBfsCode() {
        return bfsCode;
    }

    public void setBfsCode(String bfsCode) {
        this.bfsCode = bfsCode;
    }

    public OccupationLabelMapping bfsCode(String bfsCode) {
        this.bfsCode = bfsCode;
        return this;
    }

    public String getAvamCode() {
        return avamCode;
    }

    public void setAvamCode(String avamCode) {
        this.avamCode = avamCode;
    }

    public OccupationLabelMapping avamCode(String avamCode) {
        this.avamCode = avamCode;
        return this;
    }

    public String getSbn3Code() {
        return sbn3Code;
    }

    public void setSbn3Code(String sbn3Code) {
        this.sbn3Code = sbn3Code;
    }

    public OccupationLabelMapping sbn3Code(String sbn3Code) {
        this.sbn3Code = sbn3Code;
        return this;
    }

    public String getSbn5Code() {
        return sbn5Code;
    }

    public void setSbn5Code(String sbn5Code) {
        this.sbn5Code = sbn5Code;
    }

    public OccupationLabelMapping sbn5Code(String sbn5Code) {
        this.sbn5Code = sbn5Code;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OccupationLabelMapping description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OccupationLabelMapping occupationMapping = (OccupationLabelMapping) o;
        if (occupationMapping.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), occupationMapping.getId());
    }

    @Override
    public String toString() {
        return "OccupationMapping{" +
            "id=" + getId() +
            ", bfsCode='" + getBfsCode() + "'" +
            ", avamCode='" + getAvamCode() + "'" +
            ", sbn3Code='" + getSbn3Code() + "'" +
            ", sbn5Code='" + getSbn5Code() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
