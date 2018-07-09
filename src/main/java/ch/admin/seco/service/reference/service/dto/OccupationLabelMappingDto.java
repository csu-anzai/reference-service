package ch.admin.seco.service.reference.service.dto;

import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotNull;

public class OccupationLabelMappingDto {
    private UUID id;

    @NotNull
    private String bfsCode;

    @NotNull
    private String avamCode;

    @NotNull
    private String sbn3Code;

    @NotNull
    private String sbn5Code;

    @NotNull
    private String description;

    private String labelEn;

    private String labelDe;

    private String labelFr;

    private String labelIt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBfsCode() {
        return bfsCode;
    }

    public void setBfsCode(String bfsCode) {
        this.bfsCode = bfsCode;
    }

    public String getAvamCode() {
        return avamCode;
    }

    public void setAvamCode(String avamCode) {
        this.avamCode = avamCode;
    }

    public String getSbn3Code() {
        return sbn3Code;
    }

    public void setSbn3Code(String sbn3Code) {
        this.sbn3Code = sbn3Code;
    }

    public String getSbn5Code() {
        return sbn5Code;
    }

    public void setSbn5Code(String sbn5Code) {
        this.sbn5Code = sbn5Code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public void setLabelEn(String labelEn) {
        this.labelEn = labelEn;
    }

    public String getLabelDe() {
        return labelDe;
    }

    public void setLabelDe(String labelDe) {
        this.labelDe = labelDe;
    }

    public String getLabelFr() {
        return labelFr;
    }

    public void setLabelFr(String labelFr) {
        this.labelFr = labelFr;
    }

    public String getLabelIt() {
        return labelIt;
    }

    public void setLabelIt(String labelIt) {
        this.labelIt = labelIt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        OccupationLabelMappingDto that = (OccupationLabelMappingDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(bfsCode, that.bfsCode) &&
            Objects.equals(avamCode, that.avamCode) &&
            Objects.equals(sbn3Code, that.sbn3Code) &&
            Objects.equals(sbn5Code, that.sbn5Code) &&
            Objects.equals(description, that.description) &&
            Objects.equals(labelEn, that.labelEn) &&
            Objects.equals(labelDe, that.labelDe) &&
            Objects.equals(labelFr, that.labelFr) &&
            Objects.equals(labelIt, that.labelIt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bfsCode, avamCode, sbn3Code, sbn5Code, description, labelEn, labelDe, labelFr, labelIt);
    }

    @Override
    public String toString() {
        return "OccupationLabelMappingDto{" +
            "id=" + id +
            ", bfsCode='" + bfsCode + '\'' +
            ", avamCode='" + avamCode + '\'' +
            ", sbn3Code='" + sbn3Code + '\'' +
            ", sbn5Code='" + sbn5Code + '\'' +
            ", description='" + description + '\'' +
            ", labelEn='" + labelEn + '\'' +
            ", labelDe='" + labelDe + '\'' +
            ", labelFr='" + labelFr + '\'' +
            ", labelIt='" + labelIt + '\'' +
            '}';
    }
}
