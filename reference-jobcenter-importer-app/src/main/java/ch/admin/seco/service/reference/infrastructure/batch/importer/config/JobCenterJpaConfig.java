package ch.admin.seco.service.reference.infrastructure.batch.importer.config;

import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.JobCenterRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
    entityManagerFactoryRef = "jobcenterEntityManagerFactory",
    transactionManagerRef = "jobcenterTransactionManager",
    basePackageClasses = {JobCenterRepository.class}
)
class JobCenterJpaConfig {

    private final DataSource jobCenterDataSource;

    private final JpaProperties jpaProperties;

    private final HibernateProperties hibernateProperties;

    JobCenterJpaConfig(@Qualifier("jobcenterDatasource") DataSource jobcenterDataSource,
                       JpaProperties jpaProperties,
                       HibernateProperties hibernateProperties) {
        this.jobCenterDataSource = jobcenterDataSource;
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean jobcenterEntityManagerFactory() {
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(
            this.jobcenterJpaVendorAdapter(),
            this.jpaProperties.getProperties(),
            null
        );
        return builder
            .dataSource(this.jobCenterDataSource)
            .packages(JobCenterRepository.class)
            .persistenceUnit("jobcenter")
            .properties(getVendorProperties())
            .build();
    }

    @Bean
    PlatformTransactionManager jobcenterTransactionManager() {
        return new JpaTransactionManager(this.jobcenterEntityManagerFactory().getObject());
    }

    private JpaVendorAdapter jobcenterJpaVendorAdapter() {
        AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(this.jpaProperties.isShowSql());
        adapter.setDatabase(this.jpaProperties.determineDatabase(this.jobCenterDataSource));
        adapter.setDatabasePlatform(this.jpaProperties.getDatabasePlatform());
        adapter.setGenerateDdl(this.jpaProperties.isGenerateDdl());
        return adapter;
    }

    private Map<String, Object> getVendorProperties() {
        Map<String, Object> properties = this.hibernateProperties.determineHibernateProperties(this.jpaProperties.getProperties(), new HibernateSettings());
        return new LinkedHashMap<>(properties);
    }

}
