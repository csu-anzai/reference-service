package ch.admin.seco.service.reference.service.dto;

public class LocalitySuggestionDto {

    private String id;

    private String city;

    private int communalCode;

    private String cantonCode;

    public String getId() {
        return id;
    }

    public LocalitySuggestionDto id(String id) {
        this.id = id;
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public LocalitySuggestionDto city(String city) {
        this.city = city;
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCommunalCode() {
        return communalCode;
    }

    public LocalitySuggestionDto communalCode(int communalCode) {
        this.communalCode = communalCode;
        return this;
    }

    public void setCommunalCode(int communalCode) {
        this.communalCode = communalCode;
    }

    public String getCantonCode() {
        return cantonCode;
    }

    public LocalitySuggestionDto cantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
        return this;
    }

    public void setCantonCode(String cantonCode) {
        this.cantonCode = cantonCode;
    }
}
