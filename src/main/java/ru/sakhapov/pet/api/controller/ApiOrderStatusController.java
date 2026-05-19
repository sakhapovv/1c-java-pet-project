package ru.sakhapov.pet.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sakhapov.pet.api.dto.OrderStatusUpdateRequest;
import ru.sakhapov.pet.store.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class ApiOrderStatusController {

    private final OrderService orderService;

    @PostMapping("/status")
    public ResponseEntity<String> updateOrderStatus(
            @RequestBody OrderStatusUpdateRequest request
    ) {
        orderService.updateOrderStatusFrom(request);
        return ResponseEntity.ok("Order status updated");
    }
}
