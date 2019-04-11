package ch.admin.seco.service.reference.infrastructure.batch.importer.config;

import org.springframework.cloud.task.configuration.DefaultTaskConfigurer;
import org.springframework.cloud.task.configuration.TaskConfigurer;
import org.springframework.cloud.task.repository.support.TaskRepositoryInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
class CustomTaskConfigurer {

    private final DataSource batchDataSource;

    CustomTaskConfigurer(DataSource batchDataSource) {
        this.batchDataSource = batchDataSource;
    }

    @Bean
    @DependsOn("customTaskRepositoryInitializer")
    public TaskConfigurer taskConfigurer() {
        return new DefaultTaskConfigurer(this.batchDataSource);
    }

    @Bean
    public TaskRepositoryInitializer customTaskRepositoryInitializer() {
        TaskRepositoryInitializer taskRepositoryInitializer = new TaskRepositoryInitializer();
        taskRepositoryInitializer.setDataSource(this.batchDataSource);
        return taskRepositoryInitializer;
    }

}
