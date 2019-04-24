package ch.admin.seco.service.reference.infrastructure.batch.importer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

import java.util.List;

import ch.admin.seco.service.reference.infrastructure.batch.importer.fixture.JobCenterFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.JobCenter;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.JobCenterRepository;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto.JobCenterDTO;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto.ReferenceService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class JobCenterImportIntTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ReferenceService referenceService;

    @Autowired
    private JobCenterRepository jobCenterRepository;

    @Before
    public void setUp() {
        for (int i = 0; i < 100; i++) {
            JobCenter jobCenter = JobCenterFixture.create("" + i, "" + i);
            jobCenterRepository.save(jobCenter);
        }
        List<JobCenter> jobCenters = jobCenterRepository.findAll();
        assertThat(jobCenters).hasSize(100);
    }

    @Test
    public void testJobCenterImportJob() throws Exception {
        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        assertThat(jobExecution.getStatus()).as("JobExecution Status").isEqualTo(COMPLETED);
        assertThat(jobExecution.getExitStatus()).as("JobExecution ExitStatus").isEqualTo(ExitStatus.COMPLETED);

        ArgumentCaptor<JobCenterDTO> argumentCaptor = ArgumentCaptor.forClass(JobCenterDTO.class);
        verify(referenceService, times(100)).createOrUpdate(argumentCaptor.capture());
    }

    @TestConfiguration
    static class TestConfig {

        @MockBean
        ReferenceService referenceService;

        @Bean
        public JobLauncherTestUtils jobLauncherTestUtils() {
            return new JobLauncherTestUtils();
        }
    }

}
