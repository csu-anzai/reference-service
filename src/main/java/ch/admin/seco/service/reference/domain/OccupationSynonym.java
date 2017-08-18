package ch.admin.seco.service.reference.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

/**
 * A OccupationSynonym.
 */
@Entity
@Table(name = "occupation_synonym")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OccupationSynonym implements Serializable {

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

    @NotNull
    @Column(name = "language", length = 2, nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;

    @NotNull
    @Size(min = 2, max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Min(10000000)
    @Max(99999999)
    @Column(name = "external_id", nullable = false)
    private int externalId;

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

    public OccupationSynonym code(int code) {
        this.code = code;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public OccupationSynonym language(Language language) {
        this.language = language;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OccupationSynonym name(String name) {
        this.name = name;
        return this;
    }

    public int getExternalId() {
        return externalId;
    }

    public void setExternalId(int externalId) {
        this.externalId = externalId;
    }

    public OccupationSynonym externalId(int externalId) {
        this.externalId = externalId;
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
        OccupationSynonym occupationSynonym = (OccupationSynonym) o;
        if (occupationSynonym.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), occupationSynonym.getId());
    }

    @Override
    public String toString() {
        return "OccupationSynonym{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", language='" + getLanguage() + "'" +
            ", occupation='" + getName() + "'" +
            ", externalId='" + getExternalId() + "'" +
            "}";
    }
}