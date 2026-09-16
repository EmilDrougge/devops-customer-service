package org.example.customerservice;

import org.example.customerservice.dto.CustomerDTO;
import org.example.customerservice.model.Customer;
import org.example.customerservice.repositories.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class CustomerControllerIntegrationTest {
//
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
    private TestRestTemplate restTemplate;

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
        // POST /customer should persist a new row and return 201 with the customer's data
    void registerCustomerReturns201AndPersistsToDatabase() {
        CustomerDTO requestBody = new CustomerDTO(null, "John", "Doe", "john@example.com");

        ResponseEntity<CustomerDTO> response = restTemplate.postForEntity(
                "/api/customer", requestBody, CustomerDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("john@example.com");
        assertThat(customerRepository.findByEmail("john@example.com")).isPresent();
    }

    @Test
        // POST /customer should return 400 and avoid a duplicate row when the email is already registered
    void registerCustomerReturns400WhenEmailAlreadyInUse() {
        restTemplate.postForEntity("/api/customer",
                new CustomerDTO(null, "John", "Doe", "john@example.com"), CustomerDTO.class);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/customer",
                new CustomerDTO(null, "Jane", "Smith", "john@example.com"), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(customerRepository.findAll()).hasSize(1);
    }

    @Test
        // GET /customer/{id} should return 200 with the persisted customer's details
    void getCustomerReturnsOkWhenFound() {
        Customer saved = customerRepository.save(customerWith("John", "Doe", "john@example.com"));

        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/customer/" + saved.getId(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("John");
    }

    @Test
        // GET /customer/{id} should return 404 when no customer exists with that id
    void getCustomerReturns404WhenNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/customer/999999", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
        // POST /customer/{id} should persist the updated fields and return 200
    void updateCustomerReturnsOkAndPersistsChanges() {
        Customer saved = customerRepository.save(customerWith("John", "Doe", "john@example.com"));
        CustomerDTO update = new CustomerDTO(null, "Emil", "D", "emil@example.com");

        ResponseEntity<CustomerDTO> response = restTemplate.postForEntity(
                "/api/customer/" + saved.getId(), update, CustomerDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Customer reloaded = customerRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getFirstName()).isEqualTo("Emil");
        assertThat(reloaded.getEmail()).isEqualTo("emil@example.com");
    }

    @Test
        // POST /customer/{id} should return 400 when no customer exists with that id
    void updateCustomerReturns400WhenNotFound() {
        CustomerDTO update = new CustomerDTO(null, "Emil", "D", "emil@example.com");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/customer/999999", update, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
        // POST /customer/{id}/delete should return 409 when no customer exists with that id
    void deleteCustomerReturns409WhenCustomerDoesNotExist() {
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/api/customer/999999/delete", null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    private Customer customerWith(String firstName, String lastName, String email) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        return customer;
    }
}