package ch.admin.seco.service.reference.web.rest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.service.reference.domain.ReportingObligation;
import ch.admin.seco.service.reference.service.ReportingObligationService;
import ch.admin.seco.service.reference.service.dto.ProfessionCodeDTO;
import ch.admin.seco.service.reference.service.dto.ReportingObligationDTO;

@RestController
@RequestMapping("/api")
public class ReportingObligationResource {

    private final Logger log = LoggerFactory.getLogger(ReportingObligationResource.class);

    private ReportingObligationService reportingObligationService;

    public ReportingObligationResource(ReportingObligationService reportingObligationService) {
        this.reportingObligationService = reportingObligationService;
    }

    @GetMapping("/reporting-obligations/check-by/{codeType}/{code}")
    @Timed
    public ResponseEntity<ReportingObligationDTO> hasReportingObligation(ProfessionCodeDTO professionCodeDTO) {
        log.debug("REST request to check reporting obligation for Switzerland by {}", professionCodeDTO);
        return ResponseUtil.wrapOrNotFound(this.reportingObligationService
            .findReportingObligation(professionCodeDTO), createCacheHeader());
    }

    @GetMapping("/reporting-obligations/check-by-canton/{codeType}/{code}")
    @Timed
    public ResponseEntity<ReportingObligationDTO> hasReportingObligationForCanton(ProfessionCodeDTO professionCodeDTO,
        @RequestParam String cantonCode) {
        log.debug("REST request to check reporting obligation for canton '{}' by {}", cantonCode, professionCodeDTO);
        return ResponseUtil.wrapOrNotFound(this.reportingObligationService
            .findReportingObligation(professionCodeDTO, cantonCode), createCacheHeader());
    }

    @GetMapping("/reporting-obligations")
    @Timed
    public List<ReportingObligation> getReportingObligations(@RequestParam(required = false) String cantonCode) {
        log.debug("REST request to get reporting obligations by canton code: {}", cantonCode);
        return this.reportingObligationService
            .getReportingObligations(cantonCode);
    }

    private HttpHeaders createCacheHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setCacheControl(CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic().getHeaderValue());
        return httpHeaders;
    }
}
