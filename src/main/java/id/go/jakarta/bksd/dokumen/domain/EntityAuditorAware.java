package id.go.jakarta.bksd.dokumen.domain;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EntityAuditorAware implements AuditorAware<String> {
    private static final Logger logger = LoggerFactory.getLogger(EntityAuditorAware.class);

    @Override
    public Optional<String> getCurrentAuditor() {
        // Retrieve the current authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication object is not null and the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.debug("No authenticated user found.");
            return Optional.empty();
        }

        // Return the username of the authenticated user
        return Optional.ofNullable(authentication.getName());
    }
}
