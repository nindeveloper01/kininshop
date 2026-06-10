package com.phoneshop.repository;

import com.phoneshop.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PhoneRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("phoneshop_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired private PhoneRepository phoneRepository;
    @Autowired private BrandRepository brandRepository;

    private Brand apple;
    private Brand samsung;

    @BeforeEach
    void setUp() {
        phoneRepository.deleteAll();
        brandRepository.deleteAll();

        apple = brandRepository.save(Brand.builder().name("Apple").country("USA").build());
        samsung = brandRepository.save(Brand.builder().name("Samsung").country("South Korea").build());

        phoneRepository.saveAll(List.of(
            Phone.builder().model("iPhone 15 Pro").price(new BigDecimal("1199.00")).stock(50).brand(apple).build(),
            Phone.builder().model("iPhone 14").price(new BigDecimal("799.00")).stock(30).brand(apple).build(),
            Phone.builder().model("Galaxy S24 Ultra").price(new BigDecimal("1299.00")).stock(25).brand(samsung).build(),
            Phone.builder().model("Galaxy A54").price(new BigDecimal("449.00")).stock(0).brand(samsung).build()
        ));
    }

    @Test
    @DisplayName("findByBrandNameContainingIgnoreCase: finds Apple phones case-insensitively")
    void findByBrandName_caseInsensitive_returnsMatches() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Phone> result = phoneRepository.findByBrandNameContainingIgnoreCase("apple", pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Phone::getModel)
                .containsExactlyInAnyOrder("iPhone 15 Pro", "iPhone 14");
    }

    @Test
    @DisplayName("findByBrandNameContainingIgnoreCase: partial match works")
    void findByBrandName_partialMatch_returnsMatches() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Phone> result = phoneRepository.findByBrandNameContainingIgnoreCase("SAMS", pageable);

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("searchByKeyword: searches across model and brand name")
    void searchByKeyword_matchesModelAndBrand() {
        Pageable pageable = PageRequest.of(0, 10);

        // Search by model keyword
        Page<Phone> byModel = phoneRepository.searchByKeyword("galaxy", pageable);
        assertThat(byModel.getContent()).hasSize(2);

        // Search by brand keyword
        Page<Phone> byBrand = phoneRepository.searchByKeyword("apple", pageable);
        assertThat(byBrand.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("searchByKeyword: returns empty for no match")
    void searchByKeyword_noMatch_returnsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Phone> result = phoneRepository.searchByKeyword("Nokia", pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("findByStockGreaterThan: returns only in-stock phones")
    void findByStockGreaterThan_returnsInStockOnly() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Phone> result = phoneRepository.findByStockGreaterThan(0, pageable);

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent())
                .extracting(Phone::getModel)
                .doesNotContain("Galaxy A54"); // stock = 0
    }

    @Test
    @DisplayName("findAll: pagination returns correct page size")
    void findAll_pagination_returnsCorrectPage() {
        Pageable firstPage = PageRequest.of(0, 2);
        Page<Phone> page1 = phoneRepository.findAll(firstPage);

        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.getTotalElements()).isEqualTo(4);
        assertThat(page1.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("save: persists phone with brand correctly")
    void save_newPhone_persistsWithBrand() {
        Phone newPhone = Phone.builder()
                .model("Pixel 8 Pro")
                .price(new BigDecimal("999.00"))
                .stock(20)
                .brand(apple)
                .build();

        Phone saved = phoneRepository.save(newPhone);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getBrand().getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("delete: removes phone from database")
    void delete_existingPhone_removesFromDb() {
        Pageable pageable = PageRequest.of(0, 10);
        Phone toDelete = phoneRepository.findAll().get(0);

        phoneRepository.deleteById(toDelete.getId());

        assertThat(phoneRepository.findById(toDelete.getId())).isEmpty();
        assertThat(phoneRepository.findAll(pageable).getTotalElements()).isEqualTo(3);
    }
}
