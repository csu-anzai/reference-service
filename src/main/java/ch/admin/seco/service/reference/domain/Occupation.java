package ch.admin.seco.service.reference.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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

    @NotNull
    @Min(value = 10000000)
    @Max(value = 99999999)
    @Column(name = "code", nullable = false)
    private Integer code;

    @NotNull
    @Column(name = "language", length = 2, nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;

    @NotNull
    @Size(min = 2, max = 70)
    @Column(name = "name", length = 70, nullable = false)
    private String name;

    @Column(name = "name_synonyms", length = 70)
    @ElementCollection
    private Set<String> namesynonyms;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Occupation code(Integer code) {
        this.code = code;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Occupation language(Language language) {
        this.language = language;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Occupation name(String name) {
        this.name = name;
        return this;
    }

    public Set<String> getNamesynonyms() {
        return namesynonyms;
    }

    public void setNamesynonyms(Set<String> namesynonyms) {
        this.namesynonyms = namesynonyms;
    }

    public Occupation namesynonyms(Set<String> namesynonyms) {
        this.namesynonyms = namesynonyms;
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
        Occupation occupation = (Occupation) o;
        if (occupation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), occupation.getId());
    }

    @Override
    public String toString() {
        return "Occupation2{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", language='" + getLanguage() + "'" +
            ", occupation='" + getName() + "'" +
            ", namesynonyms='" + getNamesynonyms() + "'" +
            "}";
    }
}
