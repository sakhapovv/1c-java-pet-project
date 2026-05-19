package ru.sakhapov.pet.api.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sakhapov.pet.store.service.CartService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId, HttpSession session) {
        cartService.addToCart(productId, session);
        return "redirect:/products";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session) {
        cartService.removeFromCart(productId, session);
        return "redirect:/cart";
    }

    @GetMapping
    public String cart(Model model, HttpSession session) {
        model.addAttribute("items", cartService.getCart(session));
        model.addAttribute("total", cartService.getTotal(session));
        return "cart";
    }


}
