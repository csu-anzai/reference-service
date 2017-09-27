package ch.admin.seco.service.reference.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import ch.admin.seco.service.reference.domain.valueobject.Labels;

/**
 * A Occupation.
 */
@Entity
@Table(name = "occupation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Occupation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @Min(10000000)
    @Max(99999999)
    @Column(name = "code", nullable = false, unique = true)
    private int code;

    @Min(100)
    @Max(999)
    @Column(name = "classification_code", nullable = false)
    private int classificationCode;

    @Valid
    @Embedded
    private Labels labels;

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

    public Occupation code(int code) {
        this.code = code;
        return this;
    }

    public int getClassificationCode() {
        return classificationCode;
    }

    public void setClassificationCode(int classificationCode) {
        this.classificationCode = classificationCode;
    }

    public Occupation classificationCode(int classificationCode) {
        this.classificationCode = classificationCode;
        return this;
    }

    public Labels getLabels() {
        return labels;
    }

    public void setLabels(Labels labels) {
        this.labels = labels;
    }

    public Occupation labels(Labels labels) {
        this.labels = labels;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, classificationCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Occupation that = (Occupation) o;
        return code == that.code &&
            classificationCode == that.classificationCode &&
            Objects.equals(id, that.id);
    }

    @Override
    public String toString() {
        return "Occupation{" +
            "id=" + id +
            ", code=" + code +
            ", classificationCode=" + classificationCode +
            ", labels=" + labels +
            '}';
    }
}
