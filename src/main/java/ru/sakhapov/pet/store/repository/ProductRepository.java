package ru.sakhapov.pet.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sakhapov.pet.store.entity.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByExternalId(String externalId);
}
