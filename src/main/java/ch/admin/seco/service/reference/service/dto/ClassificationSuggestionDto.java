package ch.admin.seco.service.reference.service.dto;

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
}
