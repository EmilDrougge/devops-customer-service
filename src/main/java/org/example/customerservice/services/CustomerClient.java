package org.example.customerservice.services;

import org.example.customerservice.model.Customer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private String baseUrl = "http://customer-service/api";
    public Customer getCustomerById(Long id){
        return restTemplate.getForObject(baseUrl+"/customer/"+id,Customer.class);

    }
    // testar githubactionss

    public Customer getCustomerByFirstName(String name) {

       return restTemplate.getForObject(baseUrl+"/customer/{id}/"+name,Customer.class);
    }
}
