package ru.sakhapov.pet.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sakhapov.pet.api.dto.RegisterForm;
import ru.sakhapov.pet.store.entity.Role;
import ru.sakhapov.pet.store.entity.User;
import ru.sakhapov.pet.store.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        User user = User.builder()
                .externalId(UUID.randomUUID().toString())
                .name(form.getName())
                .email(form.getEmail())
                .phone(form.getPhone())
                .password(passwordEncoder.encode(form.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }
}
