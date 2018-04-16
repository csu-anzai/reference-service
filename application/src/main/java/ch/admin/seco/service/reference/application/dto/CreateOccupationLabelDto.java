package ch.admin.seco.service.reference.application.dto;

public class CreateOccupationLabelDto {

    private String professionCodeType;

    private String professionCode;

    private String classifier;

    private String languageIsoCode;

    private String label;

    public String getProfessionCode() {
        return professionCode;
    }

    public void setProfessionCode(String professionCode) {
        this.professionCode = professionCode;
    }

    public String getProfessionCodeType() {
        return professionCodeType;
    }

    public void setProfessionCodeType(String professionCodeType) {
        this.professionCodeType = professionCodeType;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getLanguageIsoCode() {
        return languageIsoCode;
    }

    public void setLanguageIsoCode(String languageIsoCode) {
        this.languageIsoCode = languageIsoCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
