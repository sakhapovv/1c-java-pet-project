package ru.sakhapov.pet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateOrderRequest {

    private String orderId;
    private Customer customer;
    private List<Item> items;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Customer {
        private String siteId;
        private String name;
        private String phone;
        private String email;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Item {
        private String productId;
        private BigDecimal quantity;
    }

}
