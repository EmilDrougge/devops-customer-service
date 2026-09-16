package org.example.customerservice.repositories;
import org.example.customerservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Optional<Object> findByEmail(String email);

}
