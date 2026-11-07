package com.vehiclerental.service;

import com.vehiclerental.model.Admin;
import com.vehiclerental.repository.AdminRepository;
import com.vehiclerental.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Business rules and database calls for admins and the dashboard.
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(AdminRepository adminRepository,
                            UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Map<String, Long> getDashboardStats() {
        // LinkedHashMap keeps the order, so the dashboard shows the numbers in this order
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("customers", adminRepository.countCustomers());
        stats.put("drivers", adminRepository.countDrivers());
        stats.put("vehicles", adminRepository.countVehicles());
        stats.put("availableVehicles", adminRepository.countAvailableVehicles());
        stats.put("bookings", adminRepository.countBookings());
        stats.put("pendingBookings", adminRepository.countPendingBookings());
        stats.put("openFeedback", adminRepository.countOpenFeedback());
        stats.put("admins", adminRepository.count());
        return stats;
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAllByOrderByFullNameAsc();
    }

    @Override
    public Optional<Admin> findById(Long id) {
        return adminRepository.findById(id);
    }

    @Override
    public boolean isEmailTaken(String email, Long excludeAdminId) {
        if (excludeAdminId == null) {
            return userRepository.existsByEmailIgnoreCase(email);
        }
        return userRepository.existsByEmailIgnoreCaseAndUserIdNot(email, excludeAdminId);
    }

    @Override
    @Transactional
    public Admin create(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setEmail(admin.getEmail().trim().toLowerCase());
        admin.setActive(true);
        return adminRepository.save(admin);
    }

    @Override
    @Transactional
    public Admin update(Long id, Admin formData) {
        Admin existing = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found: " + id));
        existing.setFullName(formData.getFullName());
        existing.setEmail(formData.getEmail().trim().toLowerCase());
        existing.setPhone(formData.getPhone());
        existing.setAdminLevel(formData.getAdminLevel());
        existing.setActive(formData.isActive());
        if (formData.getPassword() != null && !formData.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(formData.getPassword()));
        }
        return adminRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // Rule: the system must always keep at least one admin
        if (adminRepository.count() <= 1) {
            throw new IllegalStateException("You cannot delete the last admin.");
        }
        adminRepository.deleteById(id);
    }
}
