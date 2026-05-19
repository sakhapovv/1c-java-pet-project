package ru.sakhapov.pet.store.entity;


import lombok.Getter;

@Getter
public enum WebOrderStatus {
    CREATED("Создан"),
    SENT_TO_1C("Новый"),
    PAID("Оплачен"),
    SHIPPED("Отгружен"),
    FAILED("Ошибка"),
    CANCELLED("Отменен");

    private final String displayName;

    WebOrderStatus(String displayName) {
        this.displayName = displayName;
    }

}
