package org.example.customerservice;

import org.example.customerservice.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


class CustomerClientTest {

    @Test
    void getCustomerByFirstName() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);

        server.expect(requestTo("http://customer-service/customer"))
                .andRespond(withSuccess(
                        "{\"id\":1,\"firstName\":\"Daniel\",\"lastName\":\"Inserte\"}",
                        MediaType.APPLICATION_JSON));

        Customer customer = restTemplate.getForObject("http://customer-service/customer", Customer.class);

        assertNotNull(customer);
        assertEquals("Daniel", customer.getFirstName());
    }
}

