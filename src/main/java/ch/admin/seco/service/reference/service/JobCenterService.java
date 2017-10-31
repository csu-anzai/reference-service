package ch.admin.seco.service.reference.service;

import java.util.Optional;

import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.service.dto.JobCenterDto;

public interface JobCenterService {

    /**
     * Search JobCenter by code and filter it's addresses by language.
     *
     * @param code JobCenter code
     * @param language address language
     * @return JobCenterDto with only one address
     */
    Optional<JobCenterDto> findJobCenterByCode(String code, Language language);
}
