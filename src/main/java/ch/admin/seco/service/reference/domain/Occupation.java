package ch.admin.seco.service.reference.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A Occupation.
 */
@Entity
@Table(name = "occupation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "reference", type = "occupation")
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
    @Size(min = 2, max = 2)
    @Column(name = "language", length = 2, nullable = false)
    private String language;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "namesynonyms", length = 50)
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

    public Occupation code(Integer code) {
        this.code = code;
        return this;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public Occupation language(String language) {
        this.language = language;
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public Occupation name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getNamesynonyms() {
        return namesynonyms;
    }

    public Occupation namesynonyms(Set<String> namesynonyms) {
        this.namesynonyms = namesynonyms;
        return this;
    }

    public void setNamesynonyms(Set<String> namesynonyms) {
        this.namesynonyms = namesynonyms;
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
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Occupation{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", language='" + getLanguage() + "'" +
            ", name='" + getName() + "'" +
            ", namesynonyms='" + getNamesynonyms() + "'" +
            "}";
    }
}
