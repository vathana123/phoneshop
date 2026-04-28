package com.backend.phoneshop.repository;

import com.backend.phoneshop.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SaleDetailRepositoryTest {

    @Autowired
    private SaleDetailRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleProductRepository saleProductRepository;

    // -------------------------
    // Helpers
    // -------------------------
    private Product createProduct() {
        return productRepository.save(
                Product.builder()
                        .name("iPhone")
                        .availableUnit(100)
                        .build()
        );
    }

    private SaleProduct createSaleProduct() {
        return saleProductRepository.save(
                SaleProduct.builder()
                        .totalAmount(BigDecimal.ZERO)
                        .build()
        );
    }

    private SaleDetail createSaleDetail(Product product, SaleProduct saleProduct) {
        return repository.save(
                SaleDetail.builder()
                        .product(product)
                        .saleProduct(saleProduct)
                        .quantity(2)
                        .saleAmount(BigDecimal.valueOf(100))
                        .discount(0.0)
                        .build()
        );
    }

    // -------------------------
    // TESTS
    // -------------------------

    @Test
    @DisplayName("Should find by saleProductId")
    void shouldFindBySaleProductId() {
        // given
        Product product = createProduct();
        SaleProduct sp = createSaleProduct();

        SaleDetail sd = createSaleDetail(product, sp);

        // when
        List<SaleDetail> result = repository.findBySaleProductId(sp.getId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(sd.getId());
    }

    @Test
    @DisplayName("Should return empty when saleProductId not found")
    void shouldReturnEmpty_whenNotFound() {
        // when
        List<SaleDetail> result = repository.findBySaleProductId(999L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find by multiple saleProductIds")
    void shouldFindBySaleProductIdIn() {
        // given
        Product product = createProduct();

        SaleProduct sp1 = createSaleProduct();
        SaleProduct sp2 = createSaleProduct();

        createSaleDetail(product, sp1);
        createSaleDetail(product, sp2);

        // when
        List<SaleDetail> result =
                repository.findBySaleProductIdIn(List.of(sp1.getId(), sp2.getId()));

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty when no IDs match")
    void shouldReturnEmpty_whenIdListNotMatch() {
        // when
        List<SaleDetail> result =
                repository.findBySaleProductIdIn(List.of(100L, 200L));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should include canceled records (no SQLRestriction)")
    void shouldIncludeCanceledRecords() {
        // given
        Product product = createProduct();
        SaleProduct sp = createSaleProduct();

        SaleDetail sd = createSaleDetail(product, sp);

        // simulate soft delete
        sd.setCanceledAt(java.time.LocalDateTime.now());
        repository.save(sd);

        // when
        List<SaleDetail> result = repository.findBySaleProductId(sp.getId());

        // then
        assertThat(result).hasSize(1); // 🔥 still returned
    }
}