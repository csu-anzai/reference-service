package ch.admin.seco.service.reference.infrastructure.batch.importer;

import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.JobCenterRepository;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto.JobCenterDTO;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto.ReferenceService;
import ch.admin.seco.service.reference.service.dto.JobCenterDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

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

    @Test
    public void testJobCenterImportJob() throws Exception {
        //when
        when(referenceService.searchAllJobCenters()).thenReturn(prepareResponseEntity(prepareList()));
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        assertThat(jobExecution.getStatus()).as("JobExecution Status").isEqualTo(COMPLETED);
        assertThat(jobExecution.getExitStatus()).as("JobExecution ExitStatus").isEqualTo(ExitStatus.COMPLETED);

        ArgumentCaptor<JobCenterDTO> argumentCaptor = ArgumentCaptor.forClass(JobCenterDTO.class);
        verify(referenceService, times(3)).createOrUpdate(argumentCaptor.capture());
        assertEquals(3, jobCenterRepository.findAll().size());
    }

    private List<JobCenterDto> prepareList() {
        List<JobCenterDto> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(mock(JobCenterDto.class));
        }
        return list;
    }

    private ResponseEntity<List<JobCenterDto>> prepareResponseEntity(List<JobCenterDto> list) {
        return new ResponseEntity<>(list, HttpStatus.OK);
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
