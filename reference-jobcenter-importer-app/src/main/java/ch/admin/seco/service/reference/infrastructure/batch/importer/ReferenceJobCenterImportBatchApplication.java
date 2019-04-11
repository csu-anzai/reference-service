package ch.admin.seco.service.reference.infrastructure.batch.importer;


import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.task.configuration.EnableTask;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableBatchProcessing
@EnableTask
public class ReferenceJobCenterImportBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReferenceJobCenterImportBatchApplication.class, args);
    }
}
