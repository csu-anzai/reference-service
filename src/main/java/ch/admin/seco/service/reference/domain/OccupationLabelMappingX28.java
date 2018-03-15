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
    @Column(name = "avam_code", nullable = false)
    private String avamCode;

    @NotNull
    @Column(name = "x28_code", nullable = false)
    private String x28Code;

    public String getAvamCode() {
        return avamCode;
    }

    public void setAvamCode(String avamCode) {
        this.avamCode = avamCode;
    }

    public OccupationLabelMappingX28 avamCode(String avamCode) {
        this.avamCode = avamCode;
        return this;
    }

    public String getX28Code() {
        return x28Code;
    }

    public void setX28Code(String x28Code) {
        this.x28Code = x28Code;
    }

    public OccupationLabelMappingX28 x28Code(String x28Code) {
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
        return Objects.equals(id, that.id) &&
            Objects.equals(getAvamCode(), that.getAvamCode()) &&
            Objects.equals(getX28Code(), that.getX28Code());
    }

    @Override
    public String toString() {
        return "OccupationLabelMappingX28{" +
            "id=" + id +
            ", avamCode='" + avamCode + '\'' +
            ", x28Code='" + x28Code + '\'' +
            '}';
    }
}
