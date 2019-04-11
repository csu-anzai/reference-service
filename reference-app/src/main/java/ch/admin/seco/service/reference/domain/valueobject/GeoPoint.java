package ch.admin.seco.service.reference.domain.valueobject;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

@Embeddable
public class GeoPoint {

    @NotNull
    @Column(name = "latitude", nullable = false)
    @JsonProperty("lat")
    private Double latitude;

    @NotNull
    @Column(name = "longitude", nullable = false)
    @JsonProperty("lon")
    private Double longitude;

    public GeoPoint() {
    }

    public GeoPoint(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public GeoPoint latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public GeoPoint longitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    @Override
    public int hashCode() {
        int result = 1;

        long temp = Double.doubleToLongBits(latitude != null ? latitude : 0D);
        result = 31 * result + (int) (temp ^ temp >>> 32);

        temp = Double.doubleToLongBits(longitude != null ? longitude : 0D);
        result = 31 * result + (int) (temp ^ temp >>> 32);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GeoPoint geoPoint = (GeoPoint) o;

        return Double.compare(geoPoint.latitude, latitude) == 0 && Double.compare(geoPoint.longitude, longitude) == 0;
    }

    @Override
    public String toString() {
        return "GeoPoint{" +
            "latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
    }
}
