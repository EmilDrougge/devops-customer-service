package org.example.customerservice.config;
import org.example.customerservice.model.Customer;
import org.example.customerservice.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component

public class Config implements CommandLineRunner {

    private final CustomerRepository customerRepository;


    public Config(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;

    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() == 0) {
            customerRepository.saveAll(List.of(
                    new Customer( "John", "Doe", "john@example.com"),
                    new Customer( "Jane", "Smith", "jane@example.com"),
                    new Customer( "Bob", "Johnson", "bob@example.com")
            ));
        }


    }
}