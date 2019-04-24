package ch.admin.seco.service.reference.infrastructure.batch.importer.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DatasourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("reference-jobcenter-importer.datasource.batch")
    public DataSourceProperties batchDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource batchDataSource() {
        return batchDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    @ConfigurationProperties("reference-jobcenter-importer.datasource.jobcenter")
    public DataSourceProperties jobcenterDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource jobcenterDatasource() {
        return jobcenterDatasourceProperties().initializeDataSourceBuilder().build();
    }
}
