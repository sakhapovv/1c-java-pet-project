package ru.sakhapov.pet.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateRequest {

    private String orderId;
    private String status;
    private String order;
}
