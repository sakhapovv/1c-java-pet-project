package ru.sakhapov.pet.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDto {

    private String id;
    private String name;
    private String category;
    private Boolean active;
    private BigDecimal price;
    private BigDecimal stock;
    private String imageUrl;
}
