package com.vehiclerental.service;

import com.vehiclerental.model.Customer;
import com.vehiclerental.repository.CustomerRepository;
import com.vehiclerental.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * The real work for the Customer module: business rules + database calls.
 *
 * OOP: implements the CustomerService interface (Abstraction).
 *      Information hiding - JSP pages never touch the database or passwords directly.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Spring automatically passes in these objects (dependency injection)
    public CustomerServiceImpl(CustomerRepository customerRepository,
                               UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll(Sort.by("fullName"));
    }

    @Override
    public List<Customer> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String k = keyword.trim();
        return customerRepository
                .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingOrderByFullNameAsc(k, k, k);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public boolean isEmailTaken(String email, Long excludeCustomerId) {
        if (excludeCustomerId == null) {
            return userRepository.existsByEmailIgnoreCase(email);
        }
        return userRepository.existsByEmailIgnoreCaseAndUserIdNot(email, excludeCustomerId);
    }

    @Override
    @Transactional
    public Customer register(Customer customer) {
        // Never save the plain password - save the BCrypt hash instead
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setEmail(customer.getEmail().trim().toLowerCase());
        customer.setActive(true);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer update(Long id, Customer formData) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        // Copy only the fields a user is allowed to change
        existing.setFullName(formData.getFullName());
        existing.setEmail(formData.getEmail().trim().toLowerCase());
        existing.setPhone(formData.getPhone());
        existing.setAddress(formData.getAddress());
        existing.setDateOfBirth(formData.getDateOfBirth());
        existing.setActive(formData.isActive());

        // Change the password only if a new one was typed in
        if (formData.getPassword() != null && !formData.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(formData.getPassword()));
        }
        return customerRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        customerRepository.deleteById(id);
        // flush() runs the DELETE now, so a "customer has bookings" error is caught by the controller
        customerRepository.flush();
    }
}
