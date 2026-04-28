package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    // helper method
    private Product createProduct(int stock) {
        Product product = Product.builder()
                .name("iPhone")
                .availableUnit(stock)
                .build();

        return productRepository.save(product);
    }

    @Test
    @DisplayName("findByIdForUpdate should return product with lock")
    void shouldFindByIdForUpdate() {
        // given
        Product product = createProduct(10);

        // when
        Optional<Product> result = productRepository.findByIdForUpdate(product.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("decreaseStock should reduce stock when enough quantity")
    @Transactional
    void shouldDecreaseStock_whenEnoughStock() {
        // given
        Product product = createProduct(10);

        // when
        int updatedRows = productRepository.decreaseStock(product.getId(), 5);

        // then
        assertThat(updatedRows).isEqualTo(1);

        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updated.getAvailableUnit()).isEqualTo(5);
    }

    @Test
    @DisplayName("decreaseStock should not update when insufficient stock")
    @Transactional
    void shouldNotDecreaseStock_whenInsufficientStock() {
        // given
        Product product = createProduct(3);

        // when
        int updatedRows = productRepository.decreaseStock(product.getId(), 5);

        // then
        assertThat(updatedRows).isEqualTo(0);

        Product unchanged = productRepository.findById(product.getId()).orElseThrow();
        assertThat(unchanged.getAvailableUnit()).isEqualTo(3);
    }

    @Test
    @DisplayName("increaseStock should increase stock")
    @Transactional
    void shouldIncreaseStock() {
        // given
        Product product = createProduct(5);

        // when
        int updatedRows = productRepository.increaseStock(product.getId(), 10);

        // then
        assertThat(updatedRows).isEqualTo(1);

        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updated.getAvailableUnit()).isEqualTo(15);
    }
}