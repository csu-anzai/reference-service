package ch.admin.seco.service.reference.service.dto;

import java.util.Objects;

public class CantonSuggestionDto {

    private String name;

    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CantonSuggestionDto name(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CantonSuggestionDto code(String code) {
        this.code = code;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CantonSuggestionDto that = (CantonSuggestionDto) o;
        return Objects.equals(name, that.name) && Objects.equals(code, that.code);
    }
}
