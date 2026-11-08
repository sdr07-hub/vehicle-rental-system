package com.vehiclerental.service;

import com.vehiclerental.model.Admin;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * What the Admin part of the module can do.
 * OOP: Abstraction - AdminServiceImpl contains the details.
 */
public interface AdminService {

    Map<String, Long> getDashboardStats();

    List<Admin> findAll();

    Optional<Admin> findById(Long id);

    boolean isEmailTaken(String email, Long excludeAdminId);

    Admin create(Admin admin);

    Admin update(Long id, Admin formData);

    /** Deletes an admin. Throws IllegalStateException if it is the last admin. */
    void delete(Long id);
}
