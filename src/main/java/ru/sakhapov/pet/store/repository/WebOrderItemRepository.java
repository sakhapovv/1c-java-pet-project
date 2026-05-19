package ru.sakhapov.pet.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sakhapov.pet.store.entity.WebOrderItem;

public interface WebOrderItemRepository extends JpaRepository<WebOrderItem, Long> {
}