package ru.sakhapov.pet.store.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.sakhapov.pet.store.entity.Role;
import ru.sakhapov.pet.store.entity.User;
import ru.sakhapov.pet.store.repository.UserRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@mail.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin}")
    private String adminPassword;

    @Value("${app.admin.name:Admin}")
    private String adminName;

    @Value("${app.admin.phone:+70000000000}")
    private String adminPhone;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        User admin = User.builder()
                .externalId(UUID.randomUUID().toString())
                .name(adminName)
                .email(adminEmail)
                .phone(adminPhone)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();

        userRepository.save(admin);
    }
}