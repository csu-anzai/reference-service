package ch.admin.seco.service.reference.domain.search;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Id;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;

@Document(indexName = "locality", type = "localities")
@Mapping(mappingPath = "config/elasticsearch/mappings/locality.json")
public class LocalitySynonym implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private UUID id;

    private String city;

    private String zipCode;

    private int communalCode;

    private String cantonCode;

    private GeoPoint geoPoint;

    private Set<String> suggestions;

    public UUID getId() {
        return id;
    }

    public LocalitySynonym id(UUID id) {
        this.id = id;
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public LocalitySynonym city(String city) {
        this.city = city;
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public LocalitySynonym zipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getCommunalCode() {
        return communalCode;
    }

    public void setCommunalCode(int communalCode) {
        this.communalCode = communalCode;
    }

    public LocalitySynonym communalCode(int communalCode) {
        this.communalCode = communalCode;
        return this;
    }

    public String getCantonCode() {
        return cantonCode;
    }

    public LocalitySynonym cantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
        return this;
    }

    public void setCantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public LocalitySynonym geoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
        return this;
    }

    public Set<String> getSuggestions() {
        return suggestions;
    }

    public LocalitySynonym citySuggestions(Set<String> citySuggestions) {
        this.suggestions = citySuggestions;
        return this;
    }

    public void setSuggestions(Set<String> suggestions) {
        this.suggestions = suggestions;
    }
}
