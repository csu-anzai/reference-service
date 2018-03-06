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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Min(10000000)
    @Max(99999999)
    @Column(name = "bfs_code", nullable = false)
    private int bfsCode;

    @NotNull
    @Min(10000)
    @Max(999999)
    @Column(name = "avam_code", nullable = false)
    private int avamCode;

    @NotNull
    @Min(100)
    @Max(999)
    @Column(name = "sbn3_code", nullable = false)
    private int sbn3Code;

    @NotNull
    @Min(10000)
    @Max(99999)
    @Column(name = "sbn5_code", nullable = false)
    private int sbn5Code;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getBfsCode() {
        return bfsCode;
    }

    public void setBfsCode(int bfsCode) {
        this.bfsCode = bfsCode;
    }

    public OccupationLabelMapping bfsCode(int bfsCode) {
        this.bfsCode = bfsCode;
        return this;
    }

    public int getAvamCode() {
        return avamCode;
    }

    public void setAvamCode(int avamCode) {
        this.avamCode = avamCode;
    }

    public OccupationLabelMapping avamCode(int avamCode) {
        this.avamCode = avamCode;
        return this;
    }

    public int getSbn3Code() {
        return sbn3Code;
    }

    public void setSbn3Code(int sbn3Code) {
        this.sbn3Code = sbn3Code;
    }

    public OccupationLabelMapping sbn3Code(int sbn3Code) {
        this.sbn3Code = sbn3Code;
        return this;
    }

    public int getSbn5Code() {
        return sbn5Code;
    }

    public void setSbn5Code(int sbn5Code) {
        this.sbn5Code = sbn5Code;
    }

    public OccupationLabelMapping sbn5Code(int sbn5Code) {
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
