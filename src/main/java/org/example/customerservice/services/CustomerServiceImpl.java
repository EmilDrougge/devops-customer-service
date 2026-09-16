package org.example.customerservice.services;
import org.example.customerservice.dto.CustomerDTO;
import org.example.customerservice.dto.DetailedCustomerDTO;
import org.example.customerservice.model.Customer;
import org.example.customerservice.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service

public class CustomerServiceImpl implements CustomerService {

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;

    }

    private final CustomerRepository customerRepository;

    @Override
    public CustomerDTO customerToCustomerDTO(Customer c){
        return new  CustomerDTO(c.getId(),c.getFirstName(),c.getLastName(),c.getEmail());
    }
    @Override
    public DetailedCustomerDTO customerToDetailedCustomerDTO(Customer c){ //GETCUSTOMER (ta bort kommentar)
        return new DetailedCustomerDTO(c.getId(),c.getFirstName(),c.getLastName(),c.getEmail());
    }

    @Override
    public List<DetailedCustomerDTO> getAllDetailedCustomersDto() { //GETALLCUSTOMER (ta bort kommentar)
        return customerRepository.findAll().stream()
                .map(this::customerToDetailedCustomerDTO)
                .toList();
    }

    // tog bord id här tror inte vi behöver det här.
    @Override
    public Customer register(String firstName, String lastName, String email) {
        if( firstName == null || lastName == null || email == null ){
            return null;
        }
        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()){
            return null;
        }

        if (customerRepository.findByEmail(email).isPresent()){
            return null;
        }
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        return customerRepository.save(customer);
    }


   // Här ska bara Emils Endpoint skrivas där XXXXXXX står
    @Override
    public boolean deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            return false;
        }
    RestTemplate restTemplate = new RestTemplate();
        Boolean customerHasBookings;

        try{
            customerHasBookings = restTemplate.getForObject( "http://booking-service:8081/api/bookings/exists?customerId={customerId}", Boolean.class, id);

        }catch (RestClientException e){
            return false;
        }
        if(!Boolean.FALSE.equals(customerHasBookings)){
            return false;
        }
        customerRepository.deleteById(id);
        return true;
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Customer updateCustomer(Long id, String firstname, String lastname, String email) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return null;
        }
        customer.setFirstName(firstname);
        customer.setLastName(lastname);
        customer.setEmail(email);
        return customerRepository.save(customer);
    }

}
