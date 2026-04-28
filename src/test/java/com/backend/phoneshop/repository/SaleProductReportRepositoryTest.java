package com.backend.phoneshop.repository;

import com.backend.phoneshop.dto.data.SaleProductMonthlyReport;
import com.backend.phoneshop.dto.data.SaleProductReport;
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
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SaleProductReportRepositoryTest {

    @Autowired private SaleProductReportRepository repository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private CategoryTypeRepository categoryTypeRepository;
    @Autowired private SaleProductRepository saleProductRepository;
    @Autowired private SaleDetailRepository saleDetailRepository;

    // -------------------------
    // Helpers
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
                        .availableUnit(10)
                        .build()
        );
    }

    private SaleProduct createSaleProduct(LocalDateTime soldAt, BigDecimal paidAmount) {
        return saleProductRepository.save(
                SaleProduct.builder()
                        .soldAt(soldAt)
                        .paidAmount(paidAmount)
                        .build()
        );
    }

    private SaleDetail createSaleDetail(Product product, SaleProduct sp,
                                        int qty, double price) {
        return saleDetailRepository.save(
                SaleDetail.builder()
                        .product(product)
                        .saleProduct(sp)
                        .quantity(qty)
                        .saleAmount(BigDecimal.valueOf(price))
                        .discount(0.0)
                        .build()
        );
    }

    // -------------------------
    // TEST: Detail Report
    // -------------------------

    @Test
    @DisplayName("Should return sale product detail report")
    void shouldReturnDetailReport() {
        // given
        Product product = createProduct();
        SaleProduct sp = createSaleProduct(LocalDateTime.now(), BigDecimal.valueOf(1000));

        SaleDetail sd = createSaleDetail(product, sp, 2, 100);

        // when
        Page<SaleProductReport> result =
                repository.getSaleProductReport(null, null, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).isNotEmpty();

        SaleProductReport row = result.getContent().get(0);

        assertThat(row.saleProductId()).isEqualTo(sp.getId());
        assertThat(row.saleDetailId()).isEqualTo(sd.getId());
        assertThat(row.productId()).isEqualTo(product.getId());
        assertThat(row.quantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should filter detail report by date")
    void shouldFilterDetailReportByDate() {
        // given
        Product product = createProduct();

        createSaleDetail(product,
                createSaleProduct(LocalDateTime.of(2025,1,1,0,0), BigDecimal.TEN),
                1, 100);

        createSaleDetail(product,
                createSaleProduct(LocalDateTime.of(2025,2,1,0,0), BigDecimal.TEN),
                1, 100);

        // when
        Page<SaleProductReport> result =
                repository.getSaleProductReport(
                        LocalDate.of(2025,2,1),
                        null,
                        PageRequest.of(0,10)
                );

        // then
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should exclude canceled sale details")
    void shouldExcludeCanceledDetails() {
        // given
        Product product = createProduct();
        SaleProduct sp = createSaleProduct(LocalDateTime.now(), BigDecimal.TEN);

        SaleDetail active = createSaleDetail(product, sp, 2, 100);

        SaleDetail canceled = createSaleDetail(product, sp, 1, 100);
        canceled.setCanceledAt(LocalDateTime.now());
        saleDetailRepository.save(canceled);

        // when
        Page<SaleProductReport> result =
                repository.getSaleProductReport(null, null, PageRequest.of(0,10));

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).saleDetailId())
                .isEqualTo(active.getId());
    }

    // -------------------------
    // TEST: Monthly Report
    // -------------------------

    @Test
    @DisplayName("Should return monthly report with correct aggregation")
    void shouldReturnMonthlyReport() {
        // given
        Product product = createProduct();

        SaleProduct sp = createSaleProduct(
                LocalDateTime.of(2025,1,10,0,0),
                BigDecimal.valueOf(500)
        );

        createSaleDetail(product, sp, 2, 100); // 200
        createSaleDetail(product, sp, 3, 100); // 300

        // when
        Page<SaleProductMonthlyReport> result =
                repository.getMonthlyReport(null, null, PageRequest.of(0,10));

        // then
        SaleProductMonthlyReport row = result.getContent().get(0);

        assertThat(row.soleProductCount()).isEqualByComparingTo("5");
        assertThat(row.totalPaymentAmount()).isEqualByComparingTo("500");
        assertThat(row.totalPaidAmount()).isEqualByComparingTo("500");
    }

    @Test
    @DisplayName("Should filter monthly report by date")
    void shouldFilterMonthlyReport() {
        // given
        Product product = createProduct();

        createSaleDetail(product,
                createSaleProduct(LocalDateTime.of(2025,1,1,0,0), BigDecimal.TEN),
                1, 100);

        createSaleDetail(product,
                createSaleProduct(LocalDateTime.of(2025,2,1,0,0), BigDecimal.TEN),
                1, 100);

        // when
        Page<SaleProductMonthlyReport> result =
                repository.getMonthlyReport(
                        LocalDate.of(2025,2,1),
                        null,
                        PageRequest.of(0,10)
                );

        // then
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should support pagination")
    void shouldSupportPagination() {
        // given
        Product product = createProduct();

        for (int i = 0; i < 20; i++) {
            createSaleDetail(product,
                    createSaleProduct(LocalDateTime.now(), BigDecimal.TEN),
                    1, 100);
        }

        // when
        Page<SaleProductMonthlyReport> page =
                repository.getMonthlyReport(null, null, PageRequest.of(0,5));

        // then
        assertThat(page.getContent().size()).isLessThanOrEqualTo(5);
    }
}