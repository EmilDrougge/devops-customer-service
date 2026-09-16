package org.example.customerservice.services;

import org.example.customerservice.dto.CustomerDTO;
import org.example.customerservice.dto.DetailedCustomerDTO;
import org.example.customerservice.model.Customer;

import java.util.List;

public interface CustomerService {

    CustomerDTO customerToCustomerDTO(Customer c);

    DetailedCustomerDTO customerToDetailedCustomerDTO(Customer c);

    List<DetailedCustomerDTO> getAllDetailedCustomersDto();

    Customer register(String firstName, String lastName, String email);

    boolean deleteCustomer(Long id);

    Customer getCustomerById(Long id);

    Customer updateCustomer(Long id, String firstname, String lastname, String email);


}
