package ch.admin.seco.service.reference.infrastructure.batch.importer.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

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

    @Bean
    @ConditionalOnProperty(value = "reference-jobcenter-importer.datasource.jobcenter.initialization-mode", havingValue = "always")
    public DataSourceInitializer jobcenterDataSourceInitializer() {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(this.jobcenterDatasource());
        dataSourceInitializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("data-oracle.sql")));
        return dataSourceInitializer;
    }

}
