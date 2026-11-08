package com.vehiclerental.service;

import com.vehiclerental.model.Driver;
import com.vehiclerental.model.DriverAvailability;
import com.vehiclerental.model.DriverTripView;

import java.util.List;
import java.util.Optional;

/**
 * What the Driver module can do.
 * OOP: Abstraction - the controller uses this interface, DriverServiceImpl does the work.
 */
public interface DriverService {

    List<Driver> search(String keyword);

    Optional<Driver> findById(Long id);

    boolean isEmailTaken(String email, Long excludeDriverId);

    boolean isLicenceTaken(String licenceNumber, Long excludeDriverId);

    Driver register(Driver driver);

    Driver update(Long id, Driver formData);

    void updateAvailability(Long id, DriverAvailability availability);

    List<DriverTripView> findTrips(Long driverId);

    void delete(Long id);
}
