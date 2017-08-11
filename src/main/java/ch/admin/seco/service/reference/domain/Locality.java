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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;

/**
 * A Locality.
 */
@Entity
@Table(name = "locality")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "locality", type = "localities")
@Mapping(mappingPath = "config/elasticsearch/mappings/locality.json")
public class Locality implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NotNull
    @Size(max = 100)
    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @NotNull
    @Pattern(regexp = "^([1-468][0-9]|[57][0-7]|9[0-6])[0-9]{2}$")
    @Column(name = "zip_code", length = 4, nullable = false)
    private String zipCode;

    @NotNull
    @Max(9999)
    @Column(name = "communal_code", nullable = false)
    private Integer communalCode;

    @NotNull
    @Size(max = 2)
    @Column(name = "canton_code", length = 2, nullable = false)
    private String cantonCode;

    @Valid
    @NotNull
    @Embedded
    private GeoPoint geoPoint;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public Locality city(String city) {
        this.city = city;
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Locality zipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Integer getCommunalCode() {
        return communalCode;
    }

    public Locality communalCode(Integer communalCode) {
        this.communalCode = communalCode;
        return this;
    }

    public void setCommunalCode(Integer communalCode) {
        this.communalCode = communalCode;
    }

    public String getCantonCode() {
        return cantonCode;
    }

    public Locality cantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
        return this;
    }

    public void setCantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Locality geoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Locality locality = (Locality) o;
        if (locality.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), locality.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Locality{" +
            "id=" + getId() +
            ", city='" + getCity() + "'" +
            ", zipCode='" + getZipCode() + "'" +
            ", communalCode='" + getCommunalCode() + "'" +
            ", cantonCode='" + getCantonCode() + "'" +
            ", geoPoint='" + getGeoPoint() + "'" +
            "}";
    }
}
