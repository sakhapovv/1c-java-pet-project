package ru.sakhapov.pet.api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {

    @NotBlank(message = "Введите имя")
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Введите email")
    private String email;

    @NotBlank(message = "Введите телефон")
    private String phone;

    @NotBlank(message = "Введите пароль")
    private String password;
}