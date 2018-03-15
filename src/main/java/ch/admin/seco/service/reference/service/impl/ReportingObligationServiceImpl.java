package ch.admin.seco.service.reference.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.ReportingObligation;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;
import ch.admin.seco.service.reference.repository.ReportingObligationRepository;
import ch.admin.seco.service.reference.service.OccupationLabelService;
import ch.admin.seco.service.reference.service.ReportingObligationService;
import ch.admin.seco.service.reference.service.dto.ProfessionCodeDTO;
import ch.admin.seco.service.reference.service.dto.ReportingObligationDTO;

@Service
public class ReportingObligationServiceImpl implements ReportingObligationService {

    private ReportingObligationRepository reportingObligationRepository;
    private OccupationLabelService occupationLabelService;

    public ReportingObligationServiceImpl(ReportingObligationRepository reportingObligationRepository,
        OccupationLabelService occupationLabelService) {
        this.reportingObligationRepository = reportingObligationRepository;
        this.occupationLabelService = occupationLabelService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReportingObligationDTO> findReportingObligation(ProfessionCodeDTO professionCode) {
        return findReportingObligation(professionCode, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReportingObligationDTO> findReportingObligation(
        ProfessionCodeDTO professionCode,
        String cantonCode) {
        return resolveToSbn5Code(professionCode)
            .map(sbn5ProfessionCode -> checkReportingObligation(cantonCode, sbn5ProfessionCode));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportingObligation> getReportingObligations(String cantonCode) {
        if (cantonCode == null) {
            return reportingObligationRepository.findAllByCantonCodesIsNull();
        } else {
            return reportingObligationRepository.findAllByCantonCodesContaining(cantonCode);
        }
    }

    private ReportingObligationDTO checkReportingObligation(String cantonCode, ProfessionCodeDTO sbn5ProfessionCode) {
        return new ReportingObligationDTO()
            .hasReportingObligation(hasMatchingReportingObligation(sbn5ProfessionCode.getCode(), cantonCode))
            .label(resolveDefaultOccupationLabel(sbn5ProfessionCode))
            .professionCode(sbn5ProfessionCode);
    }

    private Optional<ProfessionCodeDTO> resolveToSbn5Code(ProfessionCodeDTO professionCode) {
        if (ProfessionCodeType.SBN5.equals(professionCode.getCodeType())) {
            return Optional.of(professionCode);
        } else {
            return occupationLabelService.findOneOccupationMapping(professionCode)
                .map(occupationMapping ->
                    new ProfessionCodeDTO()
                        .code(occupationMapping.getSbn5Code())
                        .codeType(ProfessionCodeType.SBN5)
                );
        }
    }

    private boolean hasMatchingReportingObligation(String sbn5Code, String cantonCode) {
        return reportingObligationRepository.findOneBySbn5Code(sbn5Code)
            .map(reportingObligation -> reportingObligation.isAppliedForSwissOrCanton(cantonCode))
            .orElse(false);
    }

    private String resolveDefaultOccupationLabel(ProfessionCodeDTO professionCode) {
        final Map<String, String> labels = occupationLabelService
            .getOccupationLabels(professionCode, Language.de);
        return labels.get("default");
    }
}
