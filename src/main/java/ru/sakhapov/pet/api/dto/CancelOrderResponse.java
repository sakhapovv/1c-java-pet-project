package ru.sakhapov.pet.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelOrderResponse {

    private String status;
    private String message;
    private String order;

    public boolean isSuccess() {
        return "cancelled".equalsIgnoreCase(status);
    }
}