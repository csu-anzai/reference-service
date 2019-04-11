package ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto;

import ch.admin.seco.service.reference.service.dto.JobCenterDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import java.util.List;

@FeignClient(name = "referenceservice", contextId = "jobcenter-api", decode404 = true)
public interface ReferenceService {

    @PatchMapping("/api/job-centers")
    void createOrUpdate(JobCenterDTO jobCenter);

    @GetMapping("/api/job-centers")
    ResponseEntity<List<JobCenterDto>> searchAllJobCenters();
}
