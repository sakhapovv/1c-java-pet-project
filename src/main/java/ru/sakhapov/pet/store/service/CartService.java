package ru.sakhapov.pet.store.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sakhapov.pet.api.dto.CartItemDto;
import ru.sakhapov.pet.store.entity.Product;
import ru.sakhapov.pet.store.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final String CART_SESSION_KEY = "cart";
    private final ProductRepository productRepository;

    public List<CartItemDto> getCart(HttpSession session) {
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute(CART_SESSION_KEY);

        if(cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }

        return cart;
    }

    public void addToCart(Long productId, HttpSession session) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Товар не найден"));

        List<CartItemDto> cart = getCart(session);

        for(CartItemDto item: cart) {
            if(item.getProduct().getId().equals(productId)) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }

        cart.add(new CartItemDto(product, 1));
    }

    public void removeFromCart(Long productId, HttpSession session) {

        List<CartItemDto> cart = getCart(session);
        cart.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public BigDecimal getTotal(HttpSession session) {
        return getCart(session).stream()
                .map(CartItemDto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
