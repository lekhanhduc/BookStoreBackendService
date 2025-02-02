package vn.khanhduc.bookstorebackend.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.khanhduc.bookstorebackend.common.UserType;
import vn.khanhduc.bookstorebackend.model.Role;
import vn.khanhduc.bookstorebackend.repository.RoleRepository;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "INIT-APPLICATION")
public class InitApp {

    private final RoleRepository roleRepository;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner initApplication() {
        log.info("Initializing application.....");
        return args -> {
            Optional<Role> roleUser = roleRepository.findByName(String.valueOf(UserType.USER));
            if(roleUser.isEmpty()) {
                roleRepository.save(Role.builder()
                                .name(String.valueOf(UserType.USER))
                                .description("User role")
                        .build());
            }

            Optional<Role> roleAdmin = roleRepository.findByName(String.valueOf(UserType.ADMIN));
            if(roleAdmin.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(String.valueOf(UserType.ADMIN))
                        .description("Admin role")
                        .build());
            }

            Optional<Role> roleManager = roleRepository.findByName(String.valueOf(UserType.MANAGER));
            if(roleManager.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(String.valueOf(UserType.MANAGER))
                        .description("Manager role")
                        .build());
            }

            Optional<Role> roleStaff = roleRepository.findByName(String.valueOf(UserType.STAFF));
            if(roleStaff.isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(String.valueOf(UserType.STAFF))
                        .description("Staff role")
                        .build());
            }
            log.info("Application initialization completed .....");
        };
    }
}
