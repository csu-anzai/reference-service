package ch.admin.seco.service.reference.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.JobCenter;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.repository.JobCenterRepository;
import ch.admin.seco.service.reference.service.JobCenterService;
import ch.admin.seco.service.reference.service.dto.JobCenterDto;
import ch.admin.seco.service.reference.service.dto.mapper.JobCenterDtoMapper;

@Service
public class JobCenterServiceImpl implements JobCenterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobCenterServiceImpl.class);

    private final JobCenterRepository jobCenterRepository;
    private final JobCenterDtoMapper jobCenterMapper;

    public JobCenterServiceImpl(JobCenterRepository jobCenterRepository,
        JobCenterDtoMapper jobCenterMapper) {

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
        LOGGER.debug("Request to suggest JobCenter by code : {} and language : {}", code, language);
        return jobCenterRepository.findOneByCode(code)
            .map(jobCenter -> jobCenterMapper.jobCenterToDto(jobCenter, language));
    }

    @Override
    public JobCenter save(JobCenter jobCenter) {
        LOGGER.debug("Request to save JobCenter : {}", jobCenter);

        jobCenterRepository.findOneByCode(jobCenter.getCode()).ifPresent(currentJobCenter -> jobCenter.setId(currentJobCenter.getId()));
        return jobCenterRepository.save(jobCenter);
    }
}
