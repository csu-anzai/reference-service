package ch.admin.seco.service.reference.service.dto;

import java.util.UUID;

public class OccupationDto {
    private UUID id;

    private int code;

    private int classificationCode;

    private LabelsDto labels;

    public UUID getId() {
        return id;
    }

    public OccupationDto id(UUID id) {
        this.id = id;
        return this;
    }

    public int getCode() {
        return code;
    }

    public OccupationDto code(int code) {
        this.code = code;
        return this;
    }

    public int getClassificationCode() {
        return classificationCode;
    }

    public OccupationDto classificationCode(int classificationCode) {
        this.classificationCode = classificationCode;
        return this;
    }

    public LabelsDto getLabels() {
        return labels;
    }

    public OccupationDto labels(String male, String female) {
        this.labels = new LabelsDto(male, female);
        return this;
    }

    private final class LabelsDto {
        private final String male;
        private final String female;

        private LabelsDto(String male, String female) {
            this.male = male;
            this.female = female;
        }

        public String getMale() {
            return male;
        }

        public String getFemale() {
            return female;
        }
    }
}
