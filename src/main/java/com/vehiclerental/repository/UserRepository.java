package com.vehiclerental.repository;

import com.vehiclerental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Shared repository for ALL users (customers, drivers and admins).
 * Used to make sure an email address is not used twice anywhere in the system.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // true if ANY user already has this email
    boolean existsByEmailIgnoreCase(String email);

    // true if a DIFFERENT user (not this id) already has this email - used when editing
    boolean existsByEmailIgnoreCaseAndUserIdNot(String email, Long userId);
}
