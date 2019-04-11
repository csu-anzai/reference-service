package ch.admin.seco.service.reference.infrastructure.batch.importer.config;

import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import io.github.jhipster.config.JHipsterProperties;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

@Configuration
public class FeignConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

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
    public Decoder feignDecoder() {
        return new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters)));
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
