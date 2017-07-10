package ch.admin.seco.service.reference.service.dto;

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
}
