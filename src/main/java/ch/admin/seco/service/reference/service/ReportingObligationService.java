package ch.admin.seco.service.reference.service;

import java.util.List;
import java.util.Optional;

import ch.admin.seco.service.reference.domain.ReportingObligation;
import ch.admin.seco.service.reference.service.dto.ProfessionCodeDTO;
import ch.admin.seco.service.reference.service.dto.ReportingObligationDTO;

public interface ReportingObligationService {

    Optional<ReportingObligationDTO> findReportingObligation(ProfessionCodeDTO professionCode);

    Optional<ReportingObligationDTO> findReportingObligation(ProfessionCodeDTO professionCode, String cantonCode);

    List<ReportingObligation> getReportingObligations(String cantonCode);
}
