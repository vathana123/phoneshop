package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void decreaseStock_shouldReduceAvailableUnits_whenEnoughStockIsAvailable() {
        Product product = persistProduct(10);

        int updatedRows = repository.decreaseStock(product.getId(), 4);

        entityManager.flush();
        entityManager.clear();

        Product reloadedProduct = entityManager.find(Product.class, product.getId());

        assertThat(updatedRows).isEqualTo(1);
        assertThat(reloadedProduct.getAvailableUnit()).isEqualTo(6);
    }

    @Test
    void decreaseStock_shouldNotUpdateAvailableUnits_whenStockIsInsufficient() {
        Product product = persistProduct(3);

        int updatedRows = repository.decreaseStock(product.getId(), 4);

        entityManager.flush();
        entityManager.clear();

        Product reloadedProduct = entityManager.find(Product.class, product.getId());

        assertThat(updatedRows).isZero();
        assertThat(reloadedProduct.getAvailableUnit()).isEqualTo(3);
    }

    private Product persistProduct(int availableUnit) {
        Product product = Product.builder()
                .name("iPhone 15 Pro")
                .availableUnit(availableUnit)
                .salePrice(new BigDecimal("999.99"))
                .build();

        entityManager.persist(product);
        entityManager.flush();
        entityManager.refresh(product);
        return product;
    }
}
