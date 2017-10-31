package ch.admin.seco.service.reference.domain;

import static java.util.Objects.nonNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
public class Occupation<T extends Occupation<T>> implements Serializable {

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
    private Labels maleLabels;

    @Valid
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "de",
            column = @Column(name = "label_de_f")),
        @AttributeOverride(name = "fr",
            column = @Column(name = "label_fr_f")),
        @AttributeOverride(name = "it",
            column = @Column(name = "label_it_f")),
        @AttributeOverride(name = "en",
            column = @Column(name = "label_en_f"))
    })
    private Labels femaleLabels;

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

    public T code(int code) {
        this.code = code;
        return (T) this;
    }

    public int getClassificationCode() {
        return classificationCode;
    }

    public void setClassificationCode(int classificationCode) {
        this.classificationCode = classificationCode;
    }

    public T classificationCode(int classificationCode) {
        this.classificationCode = classificationCode;
        return (T) this;
    }

    public Labels getMaleLabels() {
        return nonNull(maleLabels) ? maleLabels : new Labels();
    }

    public void setMaleLabels(Labels maleLabels) {
        this.maleLabels = maleLabels;
    }

    public T maleLabels(Labels maleLabels) {
        this.maleLabels = maleLabels;
        return (T) this;
    }

    public Labels getFemaleLabels() {
        return nonNull(femaleLabels) ? femaleLabels : new Labels();

    }

    public void setFemaleLabels(Labels femaleLabels) {
        this.femaleLabels = femaleLabels;
    }

    public T femaleLabels(Labels femaleLabels) {
        this.femaleLabels = femaleLabels;
        return (T) this;
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
            ", labelDe=" + (nonNull(maleLabels) ? maleLabels.getDe() : "") +
            '}';
    }
}
