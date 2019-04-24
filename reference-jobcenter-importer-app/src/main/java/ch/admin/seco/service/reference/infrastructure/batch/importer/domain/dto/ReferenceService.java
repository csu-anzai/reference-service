package ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;

@FeignClient(name = "referenceservice", contextId = "jobcenter-api", decode404 = true)
public interface ReferenceService {

    @PatchMapping("/api/job-centers")
    void createOrUpdate(JobCenterDTO jobCenter);

}
