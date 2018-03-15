package ch.admin.seco.service.reference.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ch.admin.seco.service.reference.config.converter.StringToEnumIgnoringCaseConverterFactory;

@Configuration
public class ConvertersConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // TODO when migration to Spring Boot 2.1.0 replace with Framework Impl
        // replace with org.springframework.boot.convert.StringToEnumIgnoringCaseConverterFactory
        // https://github.com/spring-projects/spring-boot/issues/12148
        registry.addConverterFactory(new StringToEnumIgnoringCaseConverterFactory());
    }
}
