package ch.admin.seco.service.reference.config;

import io.github.jhipster.config.liquibase.AsyncSpringLiquibase;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
@ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", matchIfMissing = true)
public class LiquibaseConfig {

    private final TaskExecutor taskExecutor;

    private final DataSource dataSource;

    private final LiquibaseProperties liquibaseProperties;

    private final Environment environment;

    public LiquibaseConfig(@Qualifier("taskExecutor") TaskExecutor taskExecutor,
                           DataSource dataSource,
                           LiquibaseProperties liquibaseProperties,
                           Environment environment) {
        this.taskExecutor = taskExecutor;
        this.dataSource = dataSource;
        this.liquibaseProperties = liquibaseProperties;
        this.environment = environment;
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new AsyncSpringLiquibase(taskExecutor, this.environment);
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(this.liquibaseProperties.getChangeLog());
        liquibase.setContexts(this.liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(this.liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(this.liquibaseProperties.isDropFirst());
        return liquibase;
    }
}
