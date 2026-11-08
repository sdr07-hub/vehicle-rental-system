package com.vehiclerental.service;

import com.vehiclerental.model.Customer;

import java.util.List;
import java.util.Optional;

/**
 * What the Customer module can do (the "contract").
 * The controller only knows about this interface, not how it works inside.
 *
 * OOP: Abstraction - the details are hidden in CustomerServiceImpl.
 */
public interface CustomerService {

    List<Customer> findAll();

    List<Customer> search(String keyword);

    Optional<Customer> findById(Long id);

    boolean isEmailTaken(String email, Long excludeCustomerId);

    Customer register(Customer customer);

    Customer update(Long id, Customer formData);

    void delete(Long id);
}
