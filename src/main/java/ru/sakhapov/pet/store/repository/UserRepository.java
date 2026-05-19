package ru.sakhapov.pet.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sakhapov.pet.store.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
