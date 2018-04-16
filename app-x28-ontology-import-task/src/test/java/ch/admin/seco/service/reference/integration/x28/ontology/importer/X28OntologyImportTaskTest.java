package ch.admin.seco.service.reference.integration.x28.ontology.importer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.batch.core.BatchStatus.COMPLETED;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import ch.admin.seco.service.reference.application.dto.CreateOccupationLabelDto;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
        "spring.cloud.task.closecontext_enabled=false", "spring.batch.job.enabled=false"})
public class X28OntologyImportTaskTest {
    private static final String TEST_FILE_NAME = "ontology_seco_current.xml.gz";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @MockBean
    private MessageSource<File> x28DataFileMessageSource;

    @Autowired
    private MessageCollector messageCollector;

    @Autowired
    private Source source;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    private JacksonTester<CreateOccupationLabelDto> createOccupationLabelDtoJacksonTester;


    @Before
    public void setup() {
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    public void processZipFile() throws Exception {
        Path zipFile = prepareZipFile();

        // start task
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // assert job execution
        assertThat(jobExecution.getStatus()).as("JobExecution Status").isEqualTo(COMPLETED);
        assertThat(jobExecution.getExitStatus()).as("JobExecution ExitStatus").isEqualTo(ExitStatus.COMPLETED);

        // assert messages
        assertThat(messageCollector.forChannel(source.output())).hasSize(69567);

        Message<String> message1 = (Message<String>) messageCollector.forChannel(source.output()).poll();
        assertThat(message1).isNotNull();
//        assertThat(message1.getHeaders().get(ACTION)).isEqualTo(CREATE_FROM_X28.name());
        assertThat(createOccupationLabelDtoJacksonTester.parse(message1.getPayload()).getObject().getLabel())
                .isEqualTo("Adm. Assistent");
    }

    @Test
    public void processFileNotPresent() throws Exception {

        // start task
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        // assert job execution
        assertThat(jobExecution.getStatus()).as("JobExecution Status").isEqualTo(COMPLETED);
        assertThat(jobExecution.getExitStatus()).as("JobExecution ExitStatus").isEqualTo(ExitStatus.COMPLETED);
    }

    private Path prepareZipFile() throws IOException {
        Path source = Paths.get(new ClassPathResource("data/" + TEST_FILE_NAME).getURI());
        Path target = temporaryFolder.newFile(TEST_FILE_NAME).toPath();
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        when(x28DataFileMessageSource.receive())
                .then((invocation) -> MessageBuilder.withPayload(target.toFile()).build());
        return target;
    }
}

@Configuration
class Config {

    @Bean
    JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }
}
