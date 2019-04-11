package ch.admin.seco.service.reference.service.dto;

import java.util.Set;

public class ReportingObligationDTO {

    private boolean hasReportingObligation;

    private ProfessionCodeDTO professionCode;

    private String label;

    private Set<String> cantons;

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

    public Set<String> getCantons() {
        return cantons;
    }

    public ReportingObligationDTO cantons(Set<String> cantons) {
        this.cantons = cantons;
        return this;
    }
}
