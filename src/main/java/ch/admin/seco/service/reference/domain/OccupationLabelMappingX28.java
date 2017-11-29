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
@Table(name = "occupation_label_mapping_x28")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OccupationLabelMappingX28 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NotNull
    @Min(10000)
    @Max(999999)
    @Column(name = "avam_code", nullable = false)
    private int avamCode;

    @NotNull
    @Min(10000000)
    @Max(99999999)
    @Column(name = "x28_code", nullable = false)
    private int x28Code;

    public int getAvamCode() {
        return avamCode;
    }

    public void setAvamCode(int avamCode) {
        this.avamCode = avamCode;
    }

    public OccupationLabelMappingX28 avamCode(int avamCode) {
        this.avamCode = avamCode;
        return this;
    }

    public int getX28Code() {
        return x28Code;
    }

    public void setX28Code(int x28Code) {
        this.x28Code = x28Code;
    }

    public OccupationLabelMappingX28 x28Code(int x28Code) {
        this.x28Code = x28Code;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getAvamCode(), getX28Code());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OccupationLabelMappingX28 that = (OccupationLabelMappingX28) o;
        return getAvamCode() == that.getAvamCode() &&
            getX28Code() == that.getX28Code() &&
            Objects.equals(id, that.id);
    }

    @Override
    public String toString() {
        return "OccupationMappingX28{" +
            "id=" + id +
            ", avamCode=" + avamCode +
            ", x28Code=" + x28Code +
            '}';
    }
}
