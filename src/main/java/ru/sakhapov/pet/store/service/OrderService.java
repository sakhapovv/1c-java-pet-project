package ru.sakhapov.pet.store.service;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.sakhapov.pet.api.controller.CreateOrderResponse;
import ru.sakhapov.pet.api.dto.*;
import ru.sakhapov.pet.store.entity.User;
import ru.sakhapov.pet.store.entity.WebOrder;
import ru.sakhapov.pet.store.entity.WebOrderItem;
import ru.sakhapov.pet.store.entity.WebOrderStatus;
import ru.sakhapov.pet.store.repository.UserRepository;
import ru.sakhapov.pet.store.repository.WebOrderRepository;

import java.math.BigDecimal;
import java.net.Authenticator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final WebOrderRepository webOrderRepository;
    private final RestClient restClient;
    private final CartService cartService;

    @Value("${integration.orders-url}")
    private String ordersUrl;

    @Value("${integration.cancel-order-url}")
    private String cancelOrderUrl;

    public CreateOrderResponse createOrder(HttpSession session, Authentication authentication) {

        List<CartItemDto> cart = cartService.getCart(session);

        if (cart.isEmpty()) {
            throw new RuntimeException("Корзина пустая");
        }

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        String orderId = UUID.randomUUID().toString();

        WebOrder webOrder = WebOrder.builder()
                .externalOrderId(orderId)
                .user(user)
                .status(WebOrderStatus.CREATED)
                .total(cartService.getTotal(session))
                .createdAt(LocalDateTime.now())
                .build();

        for (CartItemDto cartItem : cart) {
            BigDecimal price = cartItem.getProduct().getPrice();
            BigDecimal total = cartItem.getTotal();

            WebOrderItem orderItem = WebOrderItem.builder()
                    .order(webOrder)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(price)
                    .total(total)
                    .build();

            webOrder.getItems().add(orderItem);
        }

        webOrderRepository.save(webOrder);

        CreateOrderRequest.Customer customer =
                new CreateOrderRequest.Customer(
                        user.getExternalId(),
                        user.getName(),
                        user.getPhone(),
                        user.getEmail()
                );

        List<CreateOrderRequest.Item> items = cart.stream()
                .map(item -> new CreateOrderRequest.Item(
                        item.getProduct().getExternalId(),
                        BigDecimal.valueOf(item.getQuantity())
                ))
                .toList();

        CreateOrderRequest request = new CreateOrderRequest(orderId, customer, items);

        try {

            CreateOrderResponse response = restClient
                    .post()
                    .uri(ordersUrl)
                    .body(request)
                    .retrieve()
                    .body(CreateOrderResponse.class);

            if (response == null) {
                webOrder.setStatus(WebOrderStatus.FAILED);
                webOrder.setErrorMessage("1С не вернула ответ");
                webOrderRepository.save(webOrder);

                throw new RuntimeException("1С не вернула ответ");
            }

            if (!response.isSuccess()) {
                webOrder.setStatus(WebOrderStatus.FAILED);
                webOrder.setErrorMessage(response.getMessage());
                webOrderRepository.save(webOrder);

                throw new RuntimeException(response.getMessage());
            }

            webOrder.setStatus(WebOrderStatus.SENT_TO_1C);
            webOrder.setOrderPresentation(response.getOrder());
            webOrder.setSentTo1CAt(LocalDateTime.now());
            webOrderRepository.save(webOrder);

            cartService.clearCart(session);

            return response;

        } catch (Exception e) {
            webOrder.setStatus(WebOrderStatus.FAILED);
            webOrder.setErrorMessage(e.getMessage());
            webOrderRepository.save(webOrder);

            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void cancelOrder(Long orderId, Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        WebOrder order = webOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Нельзя отменить чужой заказ");
        }

        if (order.getStatus() == WebOrderStatus.CANCELLED) {
            return;
        }

        if (order.getStatus() != WebOrderStatus.SENT_TO_1C) {
            throw new RuntimeException("Можно отменить только заказ отправленный в 1С");
        }

        String url = cancelOrderUrl.replace("{orderId}", order.getExternalOrderId());

        try {
            CancelOrderResponse response = restClient
                    .post()
                    .uri(url)
                    .retrieve()
                    .body(CancelOrderResponse.class);

            if (response == null) {
                throw new RuntimeException("1С не вернула ответ на отмену заказа");
            }

            if (!response.isSuccess()) {
                throw new RuntimeException(response.getMessage());
            }

            order.setStatus(WebOrderStatus.CANCELLED);
            order.setErrorMessage(null);
            webOrderRepository.save(order);

        } catch (Exception e) {
            order.setErrorMessage(e.getMessage());
            webOrderRepository.save(order);

            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateOrderStatusFrom(OrderStatusUpdateRequest request) {

        WebOrder order = webOrderRepository.findByExternalOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Заказ сайта не найден: " + request.getOrderId()));

        WebOrderStatus status = mapOneCStatus(request.getStatus());

        order.setStatus(status);
        order.setOrderPresentation(request.getOrder());
        order.setErrorMessage(null);

        webOrderRepository.save(order);
    }

    private WebOrderStatus mapOneCStatus(String oneCStatus) {
        if (oneCStatus == null) {
            return WebOrderStatus.SENT_TO_1C;
        }

        return switch (oneCStatus) {
            case "Новый" -> WebOrderStatus.SENT_TO_1C;
            case "Оплачен" -> WebOrderStatus.PAID;
            case "Отгружен" -> WebOrderStatus.SHIPPED;
            case "Отменен" -> WebOrderStatus.CANCELLED;
            default -> WebOrderStatus.SENT_TO_1C;
        };
        }
}
