package ru.sakhapov.pet.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sakhapov.pet.store.repository.ProductRepository;
import ru.sakhapov.pet.store.service.ProductSyncService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ProductSyncService syncService;
    private final ProductRepository productRepository;

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("productsCount", productRepository.count());
        return "admin";
    }

    @PostMapping("/sync/products")
    public String syncProducts(Model model) {
        syncService.syncProducts();

        model.addAttribute("productsCount", productRepository.count());
        model.addAttribute("message", "Товары успешно синхронизированы из 1С");

        return "admin";
    }
}