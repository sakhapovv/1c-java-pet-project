package ru.sakhapov.pet.api.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderResponse {

    private String status;
    private String message;
    private String order;

    public boolean isSuccess() {
        return "created".equalsIgnoreCase(status);
    }
}
