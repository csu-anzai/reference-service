package ch.admin.seco.service.reference.web.rest;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.service.reference.domain.JobCenter;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.service.JobCenterService;
import ch.admin.seco.service.reference.service.dto.JobCenterDto;
import ch.admin.seco.service.reference.web.rest.util.HeaderUtil;

@RestController
@RequestMapping("/api")
public class JobCenterResource {

    private static final String ENTITY_NAME = "jobCenter";
    private static final Logger LOGGER = LoggerFactory.getLogger(JobCenterResource.class);

    private final JobCenterService jobCenterService;

    public JobCenterResource(JobCenterService jobCenterService) {
        this.jobCenterService = jobCenterService;
    }

    @GetMapping("/job-centers")
    @Timed
    public ResponseEntity<List<JobCenterDto>> searchAllJobCenters() {
        Language language = Language.safeValueOf(LocaleContextHolder.getLocale().getLanguage());
        LOGGER.debug("REST request to suggest all JobCenters by language {}", language);
        return ResponseEntity.ok()
            .body(jobCenterService.findAllJobCenters(language));
    }

    /**
     * Search for job center by code.
     *
     * @param code code
     * @return jobCenter DTO
     * @deprecated use searchJobCenterByCode instead of this
     */
    @GetMapping(value = "/job-centers", params = "code")
    @Timed
    @Deprecated
    public ResponseEntity<JobCenterDto> searchJobCenterByCodeParameter(@RequestParam String code) {
        return searchJobCenterByCode(code);
    }

    @GetMapping("/job-centers/{code}")
    @Timed
    public ResponseEntity<JobCenterDto> searchJobCenterByCode(@PathVariable String code) {
        Language language = Language.safeValueOf(LocaleContextHolder.getLocale().getLanguage());
        LOGGER.debug("REST request to suggest JobCenter by code {} and language {}", code, language);
        Optional<JobCenterDto> jobCenter = jobCenterService.findJobCenterByCode(code, language);
        return ResponseUtil.wrapOrNotFound(jobCenter);
    }


    @GetMapping("/job-centers/by-location")
    @Timed
    public ResponseEntity<JobCenterDto> searchJobCenterByLocation(
        @RequestParam String countryCode,
        @RequestParam(required = false) String postalCode) {
        Language language = Language.safeValueOf(LocaleContextHolder.getLocale().getLanguage());
        LOGGER.debug("REST request to suggest JobCenter by location: countryCode={}, postalCode={} and language {}",
            countryCode, postalCode, language);
        Optional<JobCenterDto> jobCenter = jobCenterService.findJobCenterByLocation(countryCode, postalCode, language);
        return ResponseUtil.wrapOrNotFound(jobCenter);
    }

    @PatchMapping("/job-centers")
    @Timed
    public ResponseEntity<JobCenter> createOrUpdate(@Valid @RequestBody JobCenter jobCenter) {
        LOGGER.debug("REST request to update JobCenter {}", jobCenter);
        JobCenter result = jobCenterService.save(jobCenter);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getCode()))
            .body(result);
    }
}
