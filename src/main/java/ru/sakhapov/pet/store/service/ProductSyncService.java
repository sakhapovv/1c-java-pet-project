package ru.sakhapov.pet.store.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.sakhapov.pet.api.dto.ProductDto;
import ru.sakhapov.pet.store.entity.Product;
import ru.sakhapov.pet.store.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductSyncService {

    private final ProductRepository productRepository;
    private final RestClient restClient;

    @Value("${integration.products-url}")
    private String productsUrl;

    //  GET в 1с.
    //  retrieve выполняет запрос и получаем ответ,
    //  парсим JSON из 1c в массив ProductDto
    public void syncProducts() {
        try {
            ProductDto[] products = restClient
                    .get()
                    .uri(productsUrl)
                    .retrieve()
                    .body(ProductDto[].class);

            if (products == null) {
                return;
            }

            for (ProductDto dto : products) {

                // Ищем товар в БД по externalId
                // Если товар есть обновляем, если нет добавляем
                Product product = productRepository
                        .findByExternalId(dto.getId())
                        .orElseGet(Product::new);

                product.setExternalId(dto.getId());
                product.setName(dto.getName());
                product.setCategory(dto.getCategory());
                product.setActive(dto.getActive());
                product.setPrice(dto.getPrice());
                product.setStock(dto.getStock());
                if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
                    product.setImageUrl("http://localhost" + dto.getImageUrl());
                } else {
                    product.setImageUrl(null);
                }

                productRepository.save(product);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка синхронизации товаров из 1С: " + e.getMessage(), e);
        }
    }
}
