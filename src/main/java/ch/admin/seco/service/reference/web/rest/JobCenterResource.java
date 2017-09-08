package ch.admin.seco.service.reference.web.rest;

import java.util.Optional;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.service.JobCenterService;
import ch.admin.seco.service.reference.service.dto.JobCenterDto;

@RestController
@RequestMapping("/api")
public class JobCenterResource {

    private static final Logger log = LoggerFactory.getLogger(JobCenterResource.class);

    private final JobCenterService jobCenterService;

    public JobCenterResource(JobCenterService jobCenterService) {
        this.jobCenterService = jobCenterService;
    }

    @GetMapping("/job-centers")
    @Timed
    public ResponseEntity<JobCenterDto> searchJobCenterByCode(@RequestParam String code,
        @RequestParam Language language) {
        log.debug("REST request to search JobCenter by code {} and language {}", code, language);
        Optional<JobCenterDto> jobCenter = jobCenterService.findJobCenterByCode(code, language);
        return ResponseUtil.wrapOrNotFound(jobCenter);
    }
}
