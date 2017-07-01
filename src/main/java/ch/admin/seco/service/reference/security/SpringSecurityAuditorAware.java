package ch.admin.seco.service.reference.security;

import ch.admin.seco.service.reference.config.Constants;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String userName = SecurityUtils.getCurrentUserLogin();
        return Optional.of(userName != null ? userName : Constants.SYSTEM_ACCOUNT);
    }
}
