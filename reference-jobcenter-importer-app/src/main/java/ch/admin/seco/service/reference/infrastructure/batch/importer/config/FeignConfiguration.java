package ch.admin.seco.service.reference.infrastructure.batch.importer.config;

import feign.RequestInterceptor;
import io.github.jhipster.config.JHipsterProperties;

import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

@Configuration
public class FeignConfiguration {

    @Bean
    public RequestInterceptor oAuth2RequestInterceptor(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Security.ClientAuthorization clientAuthorization = jHipsterProperties.getSecurity().getClientAuthorization();
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setUsername(clientAuthorization.getClientId());
        resourceDetails.setPassword(clientAuthorization.getClientSecret());
        resourceDetails.setAccessTokenUri(clientAuthorization.getAccessTokenUri());
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), resourceDetails);
    }

    @Bean
    public RequestInterceptor markerRequestInterceptor() {
        return template -> template.header("X-Requested-With", "XMLHttpRequest");
    }

    @Bean
    public FeignFormatterRegistrar localDateFeignFormatterRegistrar() {
        return formatterRegistry -> {
            DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
            registrar.setUseIsoFormat(true);
            registrar.registerFormatters(formatterRegistry);
        };
    }
}
