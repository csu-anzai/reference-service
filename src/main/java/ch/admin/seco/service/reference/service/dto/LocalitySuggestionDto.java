package ch.admin.seco.service.reference.service.dto;

import java.util.Objects;

public class LocalitySuggestionDto {

    private String city;

    private int communalCode;

    private String cantonCode;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalitySuggestionDto city(String city) {
        this.city = city;
        return this;
    }

    public int getCommunalCode() {
        return communalCode;
    }

    public void setCommunalCode(int communalCode) {
        this.communalCode = communalCode;
    }

    public LocalitySuggestionDto communalCode(int communalCode) {
        this.communalCode = communalCode;
        return this;
    }

    public String getCantonCode() {
        return cantonCode;
    }

    public void setCantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
    }

    public LocalitySuggestionDto cantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, communalCode, cantonCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocalitySuggestionDto that = (LocalitySuggestionDto) o;
        return communalCode == that.communalCode &&
            Objects.equals(city, that.city) &&
            Objects.equals(cantonCode, that.cantonCode);
    }
}
