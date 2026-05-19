package ru.sakhapov.pet.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.sakhapov.pet.store.entity.User;
import ru.sakhapov.pet.store.repository.UserRepository;
import ru.sakhapov.pet.store.repository.WebOrderRepository;
import ru.sakhapov.pet.store.service.OrderService;

@Controller
@RequiredArgsConstructor
public class OrderPageController {

    private final WebOrderRepository webOrderRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;

    @GetMapping("/orders")
    public String orders(Model model, Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("user not found"));

        model.addAttribute("orders", webOrderRepository.findByUserOrderByCreatedAtDesc(user));

        return "orders";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            orderService.cancelOrder(id, authentication);
            redirectAttributes.addFlashAttribute("message", "Заказ отменен");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/orders";
    }
}