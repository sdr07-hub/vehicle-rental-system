package com.vehiclerental.repository;

import com.vehiclerental.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Talks to the database for Customer objects.
 * JpaRepository already gives us save(), findById(), findAll(), deleteById() and more.
 * Spring writes the SQL for the extra method below just from its name.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Search by name OR email OR phone (case-insensitive), sorted by name
    List<Customer> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingOrderByFullNameAsc(
            String fullName, String email, String phone);
}
