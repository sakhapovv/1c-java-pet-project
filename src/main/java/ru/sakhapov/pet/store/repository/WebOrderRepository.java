package ru.sakhapov.pet.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sakhapov.pet.store.entity.User;
import ru.sakhapov.pet.store.entity.WebOrder;

import java.util.List;
import java.util.Optional;

public interface WebOrderRepository extends JpaRepository<WebOrder, Long> {

    List<WebOrder> findByUserOrderByCreatedAtDesc(User user);

    Optional<WebOrder> findByExternalOrderId(String externalOrderId);
}
