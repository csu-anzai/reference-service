package ch.admin.seco.service.reference.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import ch.admin.seco.service.reference.domain.enums.Language;

/**
 * A Occupation.
 */
@Entity
@Table(name = "occupation_label")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OccupationLabel<T extends OccupationLabel<T>> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NotNull
    @Column(name = "code", nullable = false)
    private int code;

    @NotNull
    @Size(max = 10)
    @Column(name = "type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "language", nullable = false, length = 2)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "classifier")
    @Size(max = 10)
    private String classifier;

    @NotNull
    @Size(max = 150)
    @Column(name = "label", nullable = false)
    private String label;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public T id(UUID id) {
        this.id = id;
        return (T) this;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T type(String type) {
        this.type = type;
        return (T) this;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public T language(Language language) {
        this.language = language;
        return (T) this;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public T classifier(String classifier) {
        this.classifier = classifier;
        return (T) this;
    }

    @Nonnull
    public String getLabel() {
        return label;
    }

    public void setLabel(@Nonnull String label) {
        this.label = label;
    }

    public T label(String label) {
        this.label = label;
        return (T) this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getType(), getLanguage(), getClassifier(), getLabel());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OccupationLabel<?> that = (OccupationLabel<?>) o;
        return getCode() == that.getCode() &&
            Objects.equals(getType(), that.getType()) &&
            Objects.equals(getLanguage(), that.getLanguage()) &&
            Objects.equals(getClassifier(), that.getClassifier()) &&
            Objects.equals(getLabel(), that.getLabel());
    }

    @Override
    public String toString() {
        return "OccupationLabel{" +
            "id=" + id +
            ", code=" + code +
            ", type='" + type + '\'' +
            ", language='" + language + '\'' +
            ", classifier='" + classifier + '\'' +
            ", label='" + label + '\'' +
            '}';
    }
}
