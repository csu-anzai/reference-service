package ch.admin.seco.service.reference.service.dto;

import java.util.Objects;

public class OccupationSuggestionDto {

    private String name;
    private int code;

    protected OccupationSuggestionDto() {
    }

    public OccupationSuggestionDto(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OccupationSuggestionDto that = (OccupationSuggestionDto) o;
        return getCode() == that.getCode() &&
            Objects.equals(getName(), that.getName());
    }
}
