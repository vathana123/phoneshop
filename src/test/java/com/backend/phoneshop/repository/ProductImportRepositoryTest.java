package com.backend.phoneshop.repository;

import com.backend.phoneshop.dto.data.ExpenseMonthlyReport;
import com.backend.phoneshop.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductImportRepositoryTest {

    @Autowired
    private ProductImportRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryTypeRepository categoryTypeRepository;

    // -------------------------
    // Helper: create full graph
    // -------------------------
    private Product createProduct() {
        CategoryType ct = categoryTypeRepository.save(
                CategoryType.builder().name("Electronics").build()
        );

        Brand brand = brandRepository.save(
                Brand.builder().name("Apple").build()
        );

        Category category = categoryRepository.save(
                Category.builder()
                        .name("Phone")
                        .brand(brand)
                        .categoryType(ct)
                        .build()
        );

        return productRepository.save(
                Product.builder()
                        .name("iPhone")
                        .usedStatus("NEW")
                        .category(category)
                        .availableUnit(0)
                        .build()
        );
    }

    private ProductImport createImport(Product product, int unit, double price, Date date) {
        return repository.save(
                ProductImport.builder()
                        .product(product)
                        .importUnit(unit)
                        .unitPrice(BigDecimal.valueOf(price))
                        .importDate(date)
                        .build()
        );
    }

    // -------------------------
    // TEST
    // -------------------------

    @Test
    @DisplayName("Should return monthly expense report")
    void shouldReturnMonthlyReport() {
        // given
        Product product = createProduct();

        createImport(product, 10, 100, new Date());
        createImport(product, 5, 200, new Date());

        // when
        Page<ExpenseMonthlyReport> result =
                repository.getExpenseMonthlyReport(
                        null,
                        null,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(result.getContent()).isNotEmpty();

        ExpenseMonthlyReport row = result.getContent().get(0);

        assertThat(row.productId()).isEqualTo(product.getId());
        assertThat(row.importProductCount()).isEqualTo(15L);
        assertThat(row.importCount()).isEqualTo(2L);

        // total = (10*100) + (5*200) = 1000 + 1000 = 2000
        assertThat(row.totalExpenseAmount()).isEqualByComparingTo("2000");
    }

    @Test
    @DisplayName("Should filter by date range")
    void shouldFilterByDateRange() {
        // given
        Product product = createProduct();

        createImport(product, 10, 100, java.sql.Date.valueOf("2025-01-01"));
        createImport(product, 5, 100, java.sql.Date.valueOf("2025-02-01"));

        // when
        Page<ExpenseMonthlyReport> result =
                repository.getExpenseMonthlyReport(
                        LocalDate.of(2025, 2, 1),
                        null,
                        PageRequest.of(0, 10)
                );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).importProductCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should exclude soft deleted records")
    void shouldExcludeDeleted() {
        // given
        Product product = createProduct();

        ProductImport active = createImport(product, 10, 100, new Date());

        ProductImport deleted = createImport(product, 5, 100, new Date());
        deleted.setDeletedAt(java.time.LocalDateTime.now());
        repository.save(deleted);

        // when
        Page<ExpenseMonthlyReport> result =
                repository.getExpenseMonthlyReport(null, null, PageRequest.of(0, 10));

        // then
        ExpenseMonthlyReport row = result.getContent().get(0);

        assertThat(row.importProductCount()).isEqualTo(10L);
        assertThat(row.importCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should support pagination")
    void shouldSupportPagination() {
        // given
        Product product = createProduct();

        for (int i = 0; i < 15; i++) {
            createImport(product, 1, 100, new Date());
        }

        // when
        Page<ExpenseMonthlyReport> page1 =
                repository.getExpenseMonthlyReport(null, null, PageRequest.of(0, 5));

        Page<ExpenseMonthlyReport> page2 =
                repository.getExpenseMonthlyReport(null, null, PageRequest.of(1, 5));

        // then
        assertThat(page1.getContent().size()).isLessThanOrEqualTo(5);
        assertThat(page2.getContent().size()).isLessThanOrEqualTo(5);
    }
}