package com.vehiclerental.service;

import com.vehiclerental.model.Driver;
import com.vehiclerental.model.DriverAvailability;
import com.vehiclerental.model.DriverTripView;
import com.vehiclerental.repository.DriverRepository;
import com.vehiclerental.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Business rules and database calls for drivers.
 * OOP: implements DriverService (Abstraction); hides passwords and SQL from the pages (Information hiding).
 */
@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DriverServiceImpl(DriverRepository driverRepository,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Driver> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return driverRepository.findAll(Sort.by("fullName"));
        }
        String k = keyword.trim();
        return driverRepository
                .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrLicenceNumberContainingIgnoreCaseOrderByFullNameAsc(k, k, k);
    }

    @Override
    public Optional<Driver> findById(Long id) {
        return driverRepository.findById(id);
    }

    @Override
    public boolean isEmailTaken(String email, Long excludeDriverId) {
        if (excludeDriverId == null) {
            return userRepository.existsByEmailIgnoreCase(email);
        }
        return userRepository.existsByEmailIgnoreCaseAndUserIdNot(email, excludeDriverId);
    }

    @Override
    public boolean isLicenceTaken(String licenceNumber, Long excludeDriverId) {
        if (excludeDriverId == null) {
            return driverRepository.existsByLicenceNumberIgnoreCase(licenceNumber);
        }
        return driverRepository.existsByLicenceNumberIgnoreCaseAndUserIdNot(licenceNumber, excludeDriverId);
    }

    @Override
    @Transactional
    public Driver register(Driver driver) {
        driver.setPassword(passwordEncoder.encode(driver.getPassword()));
        driver.setEmail(driver.getEmail().trim().toLowerCase());
        driver.setLicenceNumber(driver.getLicenceNumber().trim().toUpperCase());
        driver.setActive(true);
        return driverRepository.save(driver);
    }

    @Override
    @Transactional
    public Driver update(Long id, Driver formData) {
        Driver existing = driverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + id));

        existing.setFullName(formData.getFullName());
        existing.setEmail(formData.getEmail().trim().toLowerCase());
        existing.setPhone(formData.getPhone());
        existing.setLicenceNumber(formData.getLicenceNumber().trim().toUpperCase());
        existing.setLicenceExpiry(formData.getLicenceExpiry());
        existing.setYearsOfExperience(formData.getYearsOfExperience());
        existing.setAvailability(formData.getAvailability());
        existing.setActive(formData.isActive());

        if (formData.getPassword() != null && !formData.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(formData.getPassword()));
        }
        return driverRepository.save(existing);
    }

    @Override
    @Transactional
    public void updateAvailability(Long id, DriverAvailability availability) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + id));
        driver.setAvailability(availability);
        driverRepository.save(driver);
    }

    @Override
    public List<DriverTripView> findTrips(Long driverId) {
        return driverRepository.findTripsForDriver(driverId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        driverRepository.deleteById(id);
        driverRepository.flush();
    }
}
