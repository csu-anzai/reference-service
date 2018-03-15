package ch.admin.seco.service.reference.service.dto;

public class ReportingObligationDTO {

    private boolean hasReportingObligation;

    private ProfessionCodeDTO professionCode;

    private String label;

    public ReportingObligationDTO() {
    }

    public boolean isHasReportingObligation() {
        return hasReportingObligation;
    }

    public ReportingObligationDTO hasReportingObligation(boolean hasReportingObligation) {
        this.hasReportingObligation = hasReportingObligation;
        return this;
    }

    public ProfessionCodeDTO getProfessionCode() {
        return professionCode;
    }

    public ReportingObligationDTO professionCode(ProfessionCodeDTO professionCode) {
        this.professionCode = professionCode;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public ReportingObligationDTO label(String label) {
        this.label = label;
        return this;
    }
}
