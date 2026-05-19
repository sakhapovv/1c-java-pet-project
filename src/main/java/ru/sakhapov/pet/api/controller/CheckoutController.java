package ru.sakhapov.pet.api.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.sakhapov.pet.api.dto.CheckoutForm;
import ru.sakhapov.pet.store.entity.User;
import ru.sakhapov.pet.store.repository.UserRepository;
import ru.sakhapov.pet.store.service.CartService;
import ru.sakhapov.pet.store.service.OrderService;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session, Authentication authentication) {

        if (cartService.getCart(session).isEmpty()) {
            return "redirect:/cart";
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("user not found"));

        model.addAttribute("user", user);
        model.addAttribute("items", cartService.getCart(session));
        model.addAttribute("total", cartService.getTotal(session));

        return "checkout";
    }

    @PostMapping("/checkout")
    public String submitCheckout(
            Model model,
            HttpSession session,
            Authentication authentication
    ) {
        try {
            CreateOrderResponse response = orderService.createOrder(session, authentication);

            model.addAttribute("order", response.getOrder());
            return "checkout-success";

        } catch (Exception e) {
            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("user not found"));

            model.addAttribute("user", user);
            model.addAttribute("items", cartService.getCart(session));
            model.addAttribute("total", cartService.getTotal(session));
            model.addAttribute("orderError", e.getMessage());

            return "checkout";
        }
    }

    @GetMapping("/checkout/success")
    public String success() {
        return "checkout-success";
    }
}

