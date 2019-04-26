package ch.admin.seco.service.reference.service.impl;

import ch.admin.seco.service.reference.domain.JobCenter;
import ch.admin.seco.service.reference.domain.JobCenterRepository;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.service.IsAdmin;
import ch.admin.seco.service.reference.service.JobCenterService;
import ch.admin.seco.service.reference.service.dto.JobCenterDto;
import ch.admin.seco.service.reference.service.dto.mapper.JobCenterDtoMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class JobCenterServiceImpl implements JobCenterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobCenterServiceImpl.class);

    private static final String JOB_CENTER_POSTAL_CODE_MAPPING_QUERY = "select postal_code from postal_code_job_center_mapping where job_center_code = ?";

    private static final String EU_EFTA_JOB_CENTER_POSTAL_CODE = "9999";

    private static final String OTHER_JOB_CENTER_POSTAL_CODE = "9998";

    private static final List<String> CH_LIKE_COUNTRIES = Arrays.asList("CH", "LI", "FL");

    private static final List<String> EU_EFTA_COUNTRIES = Arrays.asList(
        "MT",
        "IS",
        "BE",
        "AT",
        "ES",
        "PT",
        "IE",
        "AX",
        "CZ",
        "FR",
        "PL",
        "SJ",
        "NO",
        "LT",
        "LU",
        "RO",
        "SI",
        "NL",
        "SE",
        "DE",
        "GR",
        "CY",
        "HR",
        "LI",
        "LV",
        "BG",
        "SK",
        "IT",
        "UK",
        "GB",
        "EE",
        "FI",
        "DK",
        "HU"
    );

    private final JobCenterRepository jobCenterRepository;
    private final JobCenterDtoMapper jobCenterMapper;
    private final JdbcTemplate jdbcTemplate;

    public JobCenterServiceImpl(JobCenterRepository jobCenterRepository,
        JobCenterDtoMapper jobCenterMapper,
        JdbcTemplate jdbcTemplate) {

        this.jobCenterRepository = jobCenterRepository;
        this.jobCenterMapper = jobCenterMapper;
        this.jdbcTemplate = jdbcTemplate;
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
    @Transactional(readOnly = true)
    public Optional<JobCenterDto> findJobCenterByLocation(String countryCode, String postalCode, Language language) {
        String jobCenterPostalCode = postalCode;
        if (!CH_LIKE_COUNTRIES.contains(StringUtils.upperCase(countryCode))) {
            jobCenterPostalCode = EU_EFTA_COUNTRIES.contains(StringUtils.upperCase(countryCode))
                ? EU_EFTA_JOB_CENTER_POSTAL_CODE : OTHER_JOB_CENTER_POSTAL_CODE;
        }

        return jobCenterRepository.findOneByPostalCodes(jobCenterPostalCode)
            .map(jobCenter -> jobCenterMapper.jobCenterToDto(jobCenter, language));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobCenterDto> findAllJobCenters(Language language) {
        return jobCenterRepository.findAll()
            .stream()
            .map(jobCenter -> jobCenterMapper.jobCenterToDto(jobCenter, language))
            .collect(Collectors.toList());
    }

    @Override
    @IsAdmin
    public JobCenter save(JobCenter jobCenter) {
        LOGGER.debug("Request to save JobCenter : {}", jobCenter);

        jobCenter.postalCodes(resolvePostalCode(jobCenter.getCode()));

        jobCenterRepository.findOneByCode(jobCenter.getCode()).ifPresent(currentJobCenter -> jobCenter.setId(currentJobCenter.getId()));

        return jobCenterRepository.save(jobCenter);
    }

    private Set<String> resolvePostalCode(String jobCenterCode) {
        final List<String> postalCodes = this.jdbcTemplate.queryForList(JOB_CENTER_POSTAL_CODE_MAPPING_QUERY,
            new String[] {jobCenterCode},
            String.class
        );

        return new HashSet<>(postalCodes);
    }
}