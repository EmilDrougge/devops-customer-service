package org.example.customerservice;

import org.example.customerservice.model.Customer;
import org.example.customerservice.repositories.CustomerRepository;
import org.example.customerservice.services.CustomerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class CustomerServiceIntegrationTest {

    @Container
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0")
            .withDatabaseName("customerdb")
            .withUsername("root")
            .withPassword("root");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @AfterEach
    void cleanUp() {
        customerRepository.deleteAll();
    }

    @Test
    void registerShouldPersistCustomerWhenEmailNotInUse() {
        Customer result = customerService.register("John", "Doe", "john@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(customerRepository.findByEmail("john@example.com")).isPresent();
    }

    @Test
    void registerShouldReturnNullWhenEmailAlreadyInUse() {
        customerService.register("John", "Doe", "john@example.com");

        Customer result = customerService.register("Jane", "Smith", "john@example.com");

        assertThat(result).isNull();
        assertThat(customerRepository.findAll()).hasSize(1);
    }

    @Test
    void updateCustomerShouldPersistUpdatedFields() {
        Customer saved = customerService.register("John", "Doe", "john@example.com");

        customerService.updateCustomer(saved.getId(), "Emil", "D", "emil@example.com");

        Customer reloaded = customerRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getFirstName()).isEqualTo("Emil");
        assertThat(reloaded.getLastName()).isEqualTo("D");
        assertThat(reloaded.getEmail()).isEqualTo("emil@example.com");
    }

    @Test
    void getCustomerByIdShouldReturnPersistedCustomer() {
        Customer saved = customerService.register("John", "Doe", "john@example.com");

        Customer result = customerService.getCustomerById(saved.getId());

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
    }
}