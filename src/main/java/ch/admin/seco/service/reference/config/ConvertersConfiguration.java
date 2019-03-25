package ch.admin.seco.service.reference.config;

import ch.admin.seco.service.reference.config.converter.StringToEnumIgnoringCaseConverterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConvertersConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // replace with org.springframework.boot.convert.StringToEnumIgnoringCaseConverterFactory
        // https://github.com/spring-projects/spring-boot/issues/12148
        registry.addConverterFactory(new StringToEnumIgnoringCaseConverterFactory());
    }
}
