package ch.admin.seco.service.reference.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.repository.JobCenterRepository;
import ch.admin.seco.service.reference.service.JobCenterService;
import ch.admin.seco.service.reference.service.dto.JobCenterDto;
import ch.admin.seco.service.reference.service.dto.mapper.JobCenterMapper;

@Service
public class JobCenterServiceImpl implements JobCenterService {

    private final Logger log = LoggerFactory.getLogger(JobCenterServiceImpl.class);

    private final JobCenterRepository jobCenterRepository;
    private final JobCenterMapper jobCenterMapper;

    public JobCenterServiceImpl(JobCenterRepository jobCenterRepository, JobCenterMapper jobCenterMapper) {
        this.jobCenterRepository = jobCenterRepository;
        this.jobCenterMapper = jobCenterMapper;
    }

    /**
     * Search JobCenter by code and filter it's addresses by language.
     * Fallback to Language.de if address not exists for requested language
     *
     * @param code JobCenter code
     * @param language address language
     * @return JobCenterDto with only one address
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<JobCenterDto> findJobCenterByCode(String code, Language language) {
        log.debug("Request to suggest JobCenter by code : {} and language : {}", code, language);
        return jobCenterRepository.findOneByCode(code)
            .map(jobCenter -> jobCenterMapper.jobCenterToDto(jobCenter, language));
    }
}
