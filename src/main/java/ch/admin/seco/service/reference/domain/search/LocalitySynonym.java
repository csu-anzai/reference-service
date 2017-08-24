package ch.admin.seco.service.reference.domain.search;

import java.util.Set;
import java.util.UUID;

import javax.persistence.Id;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;

@Document(indexName = "locality", type = "localities")
@Mapping(mappingPath = "config/elasticsearch/mappings/locality.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class LocalitySynonym {

    @Id
    private UUID id;

    private String city;

    private String zipCode;

    private int communalCode;

    private String cantonCode;

    private GeoPoint geoPoint;

    private Set<String> citySuggestions;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalitySynonym id(UUID id) {
        this.id = id;
        return this;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalitySynonym city(String city) {
        this.city = city;
        return this;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public LocalitySynonym zipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
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

    public void setCantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
    }

    public LocalitySynonym cantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
        return this;
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

    public Set<String> getCitySuggestions() {
        return citySuggestions;
    }

    public void setCitySuggestions(Set<String> citySuggestions) {
        this.citySuggestions = citySuggestions;
    }

    public LocalitySynonym citySuggestions(Set<String> citySuggestions) {
        this.citySuggestions = citySuggestions;
        return this;
    }
}
