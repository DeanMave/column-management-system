package io.github.deanmave.hplclims.configs;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.UserPosition;
import io.github.deanmave.hplclims.domain.UserRole;
import io.github.deanmave.hplclims.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByLogin("admin")) {
            User admin = new User();
            admin.setFirstName("Системный");
            admin.setLastName("Администратор");
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.LAB_HEAD);
            admin.setPosition(UserPosition.HEAD_OF_ANALYTICAL_LAB);
            admin.setActive(true);

            userRepository.save(admin);
            log.info("Первичный системный администратор создался с логином 'admin'");
        }
    }
}