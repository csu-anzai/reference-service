package ch.admin.seco.service.reference.infrastructure.batch.importer.config;

import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.JobCenter;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.JobCenterRepository;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto.AddressDTO;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto.JobCenterDTO;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto.ReferenceService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import feign.FeignException;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class JobCenterImportBatchConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobCenterImportBatchConfig.class);
    private static final Set<String> SHOW_EMAIL_CONTACT_FOR_JOBCENTER = ImmutableSet.of(
        "AGA10", "AGA11", "AGA20", "AGA22", "AGA30", "AGA31", "AGA32", "AGA40",
        "AGA41", "AGA70", "AGA71", "AGA72", "AGA80", "AGA81", "AGA90", "AGA91",
        "AGAA0", "AGAA1", "AGAF0", "AGAG0", "AGAH0", "AGAI0", "AGAJ0", "AGAJ1",
        "AGS00", "BAS80", "BEAF0", "BEAJ0", "BLA50", "BLAB0", "BLAC0", "BLAD0",
        "BLAE0", "BLAF0", "GEA20", "GEA30", "GEA31", "GEA40", "GEA50",
        "GEA60", "GEA70", "GEAH0", "GRD10", "GRE10", "GRF10", "GRG10", "GRH10",
        "GRI10", "LUA40", "LUA50", "LUA55", "LUA60", "LUA70", "LUA80", "LUA90",
        "NWA20", "OWA20",
        "SOA30", "SOA40", "SOA90", "SOAD0", "TGA10", "TGA30", "TGA40", "TGA50",
        "TGA90", "TGO10", "TGP10", "TGQ10", "URA20", "ZGA40");

    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    private final EmailValidator emailValidator = new EmailValidator();
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ReferenceService referenceService;
    private final JobCenterRepository jobCenterRepository;

    public JobCenterImportBatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactor, ReferenceService referenceService, JobCenterRepository jobCenterRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactor;
        this.referenceService = referenceService;
        this.jobCenterRepository = jobCenterRepository;
    }

    @Bean
    Job jobCenterImportJob() {
        return jobBuilderFactory.get("jobcenter-import-job")
            .incrementer(new RunIdIncrementer())
            .start(createOrUpdateStep())
            .build();
    }


    private TaskletStep createOrUpdateStep() {
        return stepBuilderFactory
            .get("read-and-create-step")
            .listener(jobCenterSaveSkipedItemListener())
            .listener(jobCenterItemLoggerListener())
            .<JobCenter, JobCenterDTO>chunk(10)
            .listener((ItemWriteListener<? super JobCenter>) jobCenterSaveSkipedItemListener())
            .reader(this.jobCenterRepositoryItemReader())
            .processor(mapJobCenterProcessor())
            .writer(items -> items.stream().forEach(referenceService::createOrUpdate))
            .faultTolerant()
            .skip(FeignException.class)
            .skipLimit(1000)
            .build();
    }

    @Bean
    SaveSkippedItemListener jobCenterSaveSkipedItemListener() {
        return new SaveSkippedItemListener();
    }

    @Bean
    ItemLoggerListener jobCenterItemLoggerListener() {
        return new ItemLoggerListener();
    }

    @Bean
    RepositoryItemReader<JobCenter> jobCenterRepositoryItemReader() {
        return new RepositoryItemReaderBuilder<JobCenter>()
            .repository(this.jobCenterRepository)
            .methodName("findAll")
            .sorts(ImmutableMap.of("code", Sort.Direction.ASC))
            .saveState(true)
            .name("jobcenter")
            .build();
    }

    private ItemProcessor<? super JobCenter, ? extends JobCenterDTO> mapJobCenterProcessor() {
        return jobCenter -> {
            JobCenterDTO mappedJobCenter = new JobCenterDTO();
            mappedJobCenter
                .setCode(jobCenter.getCode())
                .setEmail(mapEmail(jobCenter.getEmail(), jobCenter.getCode()))
                .setPhone(mapPhone(jobCenter.getTelefon(), jobCenter.getCode()))
                .setFax(mapPhone(jobCenter.getFax(), jobCenter.getCode()))
                .setShowContactDetailsToPublic(showPersoncalBeraterContactDetailsToPublic(jobCenter));

            Set<AddressDTO> addresses = new HashSet<>();
            addresses.add(new AddressDTO()
                .setLanguage("de")
                .setName(jobCenter.getNameDe())
                .setStreet(jobCenter.getStrasseDe())
                .setHouseNumber(jobCenter.getHausNr())
                .setZipCode(jobCenter.getPlz())
                .setCity(jobCenter.getOrtDe())
            );
            addresses.add(new AddressDTO()
                .setLanguage("fr")
                .setName(jobCenter.getNameFr())
                .setStreet(jobCenter.getStrasseFr())
                .setHouseNumber(jobCenter.getHausNr())
                .setZipCode(jobCenter.getPlz())
                .setCity(jobCenter.getOrtFr())
            );
            addresses.add(new AddressDTO()
                .setLanguage("it")
                .setName(jobCenter.getNameIt())
                .setStreet(jobCenter.getStrasseIt())
                .setHouseNumber(jobCenter.getHausNr())
                .setZipCode(jobCenter.getPlz())
                .setCity(jobCenter.getOrtIt())
            );
            mappedJobCenter.setAddresses(addresses);
            return mappedJobCenter;
        };
    }

    private boolean showPersoncalBeraterContactDetailsToPublic(JobCenter jobCenter) {
        return SHOW_EMAIL_CONTACT_FOR_JOBCENTER.contains(jobCenter.getCode());
    }

    private String mapPhone(String phone, String jobCenterCode) {
        if (StringUtils.hasText(phone)) {
            try {
                Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phone, "CH");
                if (phoneNumberUtil.isValidNumber(phoneNumber)) {
                    return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                }
            } catch (NumberParseException e) {
                LOGGER.warn("JobCenter {} has invalid phone number: {}", jobCenterCode, phone);
            }
        }
        return null;
    }

    private String mapEmail(String email, String jobCenterCode) {
        if (StringUtils.hasText(email)) {
            email = StringUtils.trimAllWhitespace(email.replace("'", ""));
            if (emailValidator.isValid(email, null)) {
                return email;
            } else {
                LOGGER.warn("User {} has invalid email: {}", jobCenterCode, email);
            }
        }
        return null;
    }

    private static class ItemLoggerListener extends ItemListenerSupport<JobCenter, JobCenterDTO> {
        private static final Logger LOGGER = LoggerFactory.getLogger(ItemLoggerListener.class);

        @Override
        public void afterRead(JobCenter item) {
            LOGGER.debug("Successfully read jobCenter: {}", item.getCode());
        }

        @Override
        public void onReadError(Exception ex) {
            LOGGER.error("JobCenter read failure", ex);
        }

        @Override
        public void onProcessError(JobCenter item, @NonNull Exception e) {
            LOGGER.error("JobCenter ({}) process failed", item.getCode());
            LOGGER.error("JobCenter process failure", e);
        }

        @Override
        public void afterWrite(List<? extends JobCenterDTO> item) {
            LOGGER.debug("JobCenters successfully saved: {}", item);
        }

        @Override
        public void onWriteError(Exception ex, List<? extends JobCenterDTO> item) {
            LOGGER.error("JobCenter write failed: {}", item);
            LOGGER.error("JobCenter write failure", ex);
        }
    }
}
