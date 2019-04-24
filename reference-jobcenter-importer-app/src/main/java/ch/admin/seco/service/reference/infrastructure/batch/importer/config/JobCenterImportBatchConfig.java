package ch.admin.seco.service.reference.infrastructure.batch.importer.config;

import java.util.List;

import com.google.common.collect.ImmutableMap;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.JobCenter;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.JobCenterRepository;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto.JobCenterDTO;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto.ReferenceService;

@Configuration
public class JobCenterImportBatchConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobCenterImportBatchConfig.class);

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
            .processor(this.jobCenterProcessor())
            .writer(items -> items.forEach(referenceService::createOrUpdate))
            .faultTolerant()
            .skip(FeignException.class)
            .skipLimit(1000)
            .build();
    }


    private JobCenterProcessor jobCenterProcessor() {
        return new JobCenterProcessor();
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
