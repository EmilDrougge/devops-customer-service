package org.example.customerservice;

import org.example.customerservice.controllers.CustomerController;
import org.example.customerservice.repositories.CustomerRepository;
import org.example.customerservice.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private CustomerRepository customerRepository;

    @Test
    void registerShouldReturnBadRequestWhenEmailAlreadyInUse() throws Exception {
        when(customerService.register(anyString(), anyString(), anyString())).thenReturn(null);

        mockMvc.perform(post("/api/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Anna","lastName":"Doe","email":"taken@example.com"}
                                """))
                .andExpect(status().isBadRequest());
    }
}