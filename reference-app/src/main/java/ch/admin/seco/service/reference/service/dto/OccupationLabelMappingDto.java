package ch.admin.seco.service.reference.service.dto;

import java.util.UUID;

public class OccupationLabelMappingDto {

    private UUID id;

    private String bfsCode;

    private String avamCode;

    private String sbn3Code;

    private String sbn5Code;

    private String iscoCode;

    private String description;

    public OccupationLabelMappingDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OccupationLabelMappingDto id(UUID id) {
        this.id = id;
        return this;
    }

    public String getBfsCode() {
        return bfsCode;
    }

    public void setBfsCode(String bfsCode) {
        this.bfsCode = bfsCode;
    }

    public OccupationLabelMappingDto bfsCode(String bfsCode) {
        this.bfsCode = bfsCode;
        return this;
    }

    public String getAvamCode() {
        return avamCode;
    }

    public void setAvamCode(String avamCode) {
        this.avamCode = avamCode;
    }

    public OccupationLabelMappingDto avamCode(String avamCode) {
        this.avamCode = avamCode;
        return this;
    }

    public String getSbn3Code() {
        return sbn3Code;
    }

    public void setSbn3Code(String sbn3Code) {
        this.sbn3Code = sbn3Code;
    }

    public OccupationLabelMappingDto sbn3Code(String sbn3Code) {
        this.sbn3Code = sbn3Code;
        return this;
    }

    public OccupationLabelMappingDto sbn5Code(String sbn5Code) {
        this.sbn5Code = sbn5Code;
        return this;
    }

    public String getSbn5Code() {
        return sbn5Code;
    }

    public void setSbn5Code(String sbn5Code) {
        this.sbn5Code = sbn5Code;
    }

    public String getIscoCode() {
        return iscoCode;
    }

    public void setIscoCode(String iscoCode) {
        this.iscoCode = iscoCode;
    }

    public OccupationLabelMappingDto iscoCode(String iscoCode) {
        this.iscoCode = iscoCode;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OccupationLabelMappingDto description(String description) {
        this.description = description;
        return this;
    }
}
