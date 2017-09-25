package ch.admin.seco.service.reference.service.dto;

import java.util.Objects;

public class ClassificationSuggestionDto {

    private String name;
    private int code;

    protected ClassificationSuggestionDto() {
    }

    public ClassificationSuggestionDto(String name, int code) {
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
        return Objects.hash(code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassificationSuggestionDto that = (ClassificationSuggestionDto) o;
        return code == that.code;
    }
}
