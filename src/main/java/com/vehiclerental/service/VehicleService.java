package com.vehiclerental.service;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleForm;

import java.util.List;
import java.util.Optional;

public interface VehicleService {

    List<Vehicle> search(String keyword);

    Optional<Vehicle> findById(Long id);

    boolean isRegistrationTaken(String registrationNumber, Long excludeVehicleId);

    Vehicle create(VehicleForm form);

    Vehicle update(Long id, VehicleForm form);

    void delete(Long id);
}
