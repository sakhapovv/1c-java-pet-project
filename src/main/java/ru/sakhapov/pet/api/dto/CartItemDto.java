package ru.sakhapov.pet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.sakhapov.pet.store.entity.Product;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CartItemDto {

    private Product product;
    private int quantity;

    public BigDecimal getTotal() {
        if(product.getPrice() == null) {
            return BigDecimal.ZERO;
        }

        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
