package ru.sakhapov.pet.store.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Product id from 1c
    @Column(name = "external_id", nullable = false, unique = true)
    String externalId;

    String name;
    String category;
    Boolean active;
    BigDecimal price;
    BigDecimal stock;
    String imageUrl;
}
