package ch.admin.seco.service.reference.integration.x28.ontology.importer;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.task.configuration.EnableTask;

import ch.admin.seco.service.reference.integration.x28.ontology.importer.config.X28Properties;

@SpringBootApplication
@EnableTask
@EnableBatchProcessing
@EnableBinding(Source.class)
@EnableConfigurationProperties(X28Properties.class)
public class X28ObligationImportTask {

    public static void main(String[] args) {
        SpringApplication.run(X28ObligationImportTask.class, args);
    }
}
