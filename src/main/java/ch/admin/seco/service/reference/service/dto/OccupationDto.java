package ch.admin.seco.service.reference.service.dto;

import java.util.Objects;
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

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCode(), getClassificationCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OccupationDto that = (OccupationDto) o;
        return getCode() == that.getCode() &&
            getClassificationCode() == that.getClassificationCode() &&
            Objects.equals(getId(), that.getId());
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
