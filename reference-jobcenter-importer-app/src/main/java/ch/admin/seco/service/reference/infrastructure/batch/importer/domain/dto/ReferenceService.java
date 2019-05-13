package ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto;

import ch.admin.seco.alv.shared.feign.AlvAuthorizedFeignClient;
import org.springframework.web.bind.annotation.PatchMapping;

@AlvAuthorizedFeignClient(name = "referenceservice", contextId = "jobcenter-api")
public interface ReferenceService {

    @PatchMapping("/api/job-centers")
    void createOrUpdate(JobCenterDTO jobCenter);

}
