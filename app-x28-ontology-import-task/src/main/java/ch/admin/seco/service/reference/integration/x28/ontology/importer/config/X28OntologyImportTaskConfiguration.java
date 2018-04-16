package ch.admin.seco.service.reference.integration.x28.ontology.importer.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import ch.admin.seco.service.reference.application.dto.CreateOccupationLabelDto;
import ch.admin.seco.service.reference.integration.x28.ontology.importer.Field;
import ch.admin.seco.service.reference.integration.x28.ontology.importer.Row;

@Configuration
public class X28OntologyImportTaskConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(X28OntologyImportTaskConfiguration.class);
    private static final String PARAMETER_XML_FILE_PATH = "XML_FILE_PATH";
    private static final String PARAMETER_LAST_MODIFIED_TIME = "LAST_MODIFIED_TIME";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MessageChannel messageBrokerOutputChannel;
    private final MessageSource<File> x28OntologyDataFileMessageSource;

    @Autowired
    public X28OntologyImportTaskConfiguration(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            MessageSource<File> x28OntologyDataFileMessageSource,
            MessageChannel output) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.x28OntologyDataFileMessageSource = x28OntologyDataFileMessageSource;
        this.messageBrokerOutputChannel = output;
    }

    @Bean
    Job x28ImportJob(
            StaxEventItemReader<Row> xmlFileReader,
            ItemProcessor<Row, CreateOccupationLabelDto> occupationLabelDtoItemProcessor,
            X28OntologyWriter x28JobAdWriter) {
        return jobBuilderFactory.get("x28-jobad-xml-import")
                .incrementer(new RunIdIncrementer())
                .listener(new CleanupXmlFileJobExecutionListener())
                .start(stepBuilderFactory
                        .get("download-from-sftp")
                        .tasklet(downloadFromSftpServer())
                        .build())
                .on("NO_FILE").end()
                .on("*")
                .to(stepBuilderFactory
                        .get("send-to-reference-service")
                        .<Row, CreateOccupationLabelDto>chunk(10)
                        .reader(xmlFileReader)
                        .processor(occupationLabelDtoItemProcessor)
                        .writer(x28JobAdWriter)
                        .build())
                .build()
                .build();
    }

    @Bean
    Tasklet downloadFromSftpServer() {
        return (contribution, chunkContext) -> {
            Message<File> x28OntologyDataFileMessage = x28OntologyDataFileMessageSource.receive();
            if ((x28OntologyDataFileMessage == null) || (x28OntologyDataFileMessage.getPayload() == null)) {

                LOG.error("X28 Ontology data file not available");
                contribution.setExitStatus(new ExitStatus("NO_FILE"));
                return RepeatStatus.FINISHED;
            }

            File xmlFile = x28OntologyDataFileMessage.getPayload();

            ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
            executionContext.put(PARAMETER_XML_FILE_PATH, xmlFile);
            executionContext.put(PARAMETER_LAST_MODIFIED_TIME, getLastModifiedTime(xmlFile));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    Jaxb2Marshaller X28Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(Row.class);
        return jaxb2Marshaller;
    }

    @Bean
    X28OntologyWriter x28JobAdWriter() {
        return new X28OntologyWriter(messageBrokerOutputChannel);
    }

    @Bean
    @JobScope
    StaxEventItemReader<Row> xmlFileReader(@Value("#{jobExecutionContext['" + PARAMETER_XML_FILE_PATH + "']}") File xmlFile) {
        return new StaxEventItemReaderBuilder<Row>()
                .resource(new PathResource(xmlFile.toPath()) {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new GZIPInputStream(super.getInputStream());
                    }
                })
                .unmarshaller(X28Marshaller())
                .strict(false)
                .saveState(false)
                .addFragmentRootElements("row")
                .build();
    }

    @Bean
    ItemProcessor<Row, CreateOccupationLabelDto> occupationLabelDtoItemProcessor() {
        return row ->
                getValue(row, "syn_id")
                        .map(id -> {
                            CreateOccupationLabelDto createOccupationLabelDto = new CreateOccupationLabelDto();
                            createOccupationLabelDto.setClassifier(id);
                            createOccupationLabelDto.setLabel(getValue(row, "syn_name").orElse(null));
                            createOccupationLabelDto.setLanguageIsoCode(getValue(row, "syn_lan").map(String::toLowerCase).orElse(null));
                            createOccupationLabelDto.setProfessionCode(getValue(row, "syn_fulID").orElse(null));
                            createOccupationLabelDto.setProfessionCodeType("X28");
                            return createOccupationLabelDto;
                        })
                        .orElse(null);
    }

    private Optional<String> getValue(Row row, String fieldName) {
        return row.getField().stream()
                .filter(field -> fieldName.equals(field.getName()))
                .map(Field::getValue)
                .findFirst();
    }

    private void delete(File file) {
        if (Files.isWritable(file.toPath())) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                file.deleteOnExit();
                LOG.error("Failed to delete file {}", file.getAbsolutePath());
            }
        }
    }

    private Date getLastModifiedTime(File xmlFile) {
        try {
            return new Date(Files.getLastModifiedTime(xmlFile.toPath()).toMillis());
        } catch (IOException e) {
            return new Date();
        }
    }

    private class CleanupXmlFileJobExecutionListener extends JobExecutionListenerSupport {
        @Override
        public void afterJob(JobExecution jobExecution) {
            if (jobExecution.getExecutionContext().containsKey(PARAMETER_XML_FILE_PATH)) {

                File xmlFile = (File) (jobExecution.getExecutionContext().get(PARAMETER_XML_FILE_PATH));
                delete(xmlFile);
            }
        }
    }
}
