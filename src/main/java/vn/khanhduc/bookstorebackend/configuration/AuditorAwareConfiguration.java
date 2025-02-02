package vn.khanhduc.bookstorebackend.configuration;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class AuditorAwareConfiguration implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getName() != null) {
            return Optional.ofNullable(authentication.getName());
        }
        return Optional.of("anonymous-user");
    }
}
