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
@Table(name = "occupation_mapping")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OccupationMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NotNull
    @Min(10000000)
    @Max(99999999)
    @Column(name = "code", nullable = false)
    private int code;

    @Min(10000000)
    @Max(99999999)
    @Column(name = "x28_code", nullable = true, unique = true)
    private Integer x28Code;

    @NotNull
    @Min(10000)
    @Max(999999)
    @Column(name = "avam_code", nullable = false)
    private int avamCode;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public OccupationMapping code(int code) {
        this.code = code;
        return this;
    }

    public Integer getX28Code() {
        return x28Code;
    }

    public void setX28Code(Integer x28Code) {
        this.x28Code = x28Code;
    }

    public OccupationMapping x28Code(Integer x28Code) {
        this.x28Code = x28Code;
        return this;
    }

    public int getAvamCode() {
        return avamCode;
    }

    public void setAvamCode(int avamCode) {
        this.avamCode = avamCode;
    }

    public OccupationMapping avamCode(int avamCode) {
        this.avamCode = avamCode;
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
        OccupationMapping occupationMapping = (OccupationMapping) o;
        if (occupationMapping.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), occupationMapping.getId());
    }

    @Override
    public String toString() {
        return "OccupationMapping{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", x28Code='" + getX28Code() + "'" +
            ", avamCode='" + getAvamCode() + "'" +
            "}";
    }
}
