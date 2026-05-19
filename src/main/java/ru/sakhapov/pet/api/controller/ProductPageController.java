package ru.sakhapov.pet.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.sakhapov.pet.store.repository.ProductRepository;

@Controller
@RequiredArgsConstructor
public class ProductPageController {

    private final ProductRepository productRepository;

    @GetMapping({"/", "/products"})
    public String products(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "products";
    }
}
