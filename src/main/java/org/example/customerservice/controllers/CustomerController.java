package org.example.customerservice.controllers;
import org.example.customerservice.dto.CustomerDTO;
import org.example.customerservice.dto.DetailedCustomerDTO;
import org.example.customerservice.model.Customer;
import org.example.customerservice.repositories.CustomerRepository;
import org.example.customerservice.services.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository repo;

    public CustomerController(CustomerService customerService, CustomerRepository repo) {
        this.customerService = customerService;
        this.repo = repo;
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<DetailedCustomerDTO> getCustomer(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customerService.customerToDetailedCustomerDTO(customer));
    }

    @PostMapping("/customer")
    public ResponseEntity<CustomerDTO> registerCustomer(@RequestBody CustomerDTO customer) {
        Customer saved = customerService.register(customer.getFirstName(), customer.getLastName(), customer.getEmail());
        if (saved == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.customerToCustomerDTO(saved));
    }

    @PostMapping("/customer/{id}")
    public ResponseEntity<CustomerDTO> updaterCustomer(@PathVariable Long id, @RequestBody CustomerDTO customer) {
        Customer updated = customerService.updateCustomer(id, customer.getFirstName(), customer.getLastName(), customer.getEmail());
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(customerService.customerToCustomerDTO(updated));
    }

    @PostMapping("/customer/{id}/delete")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        boolean result = customerService.deleteCustomer(id);
        if (!result) {
            // Om kund har aktiv bokning, ELLER inte existerar
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.noContent().build();
    }
}




