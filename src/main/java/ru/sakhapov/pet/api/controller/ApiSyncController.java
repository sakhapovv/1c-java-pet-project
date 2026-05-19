package ru.sakhapov.pet.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sakhapov.pet.store.service.ProductSyncService;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/sync")
public class ApiSyncController {

    private final ProductSyncService productSyncService;

    @PostMapping("/products")
    public ResponseEntity<String> syncProducts() {

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(2000);
                productSyncService.syncProducts();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return ResponseEntity.accepted().body("products sync scheduled");
    }
}