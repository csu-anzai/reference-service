package ch.admin.seco.service.reference.service.dto;

import java.util.Objects;

public class GeoPointDto {
    private Double lat;
    private Double lon;

    public GeoPointDto(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public GeoPointDto setLat(Double lat) {
        this.lat = lat;
        return this;
    }

    public Double getLon() {
        return lon;
    }

    public GeoPointDto setLon(Double lon) {
        this.lon = lon;
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
        GeoPointDto that = (GeoPointDto) o;
        return Objects.equals(lat, that.lat) &&
            Objects.equals(lon, that.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
