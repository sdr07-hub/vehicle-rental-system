package com.vehiclerental.service;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleFactory;
import com.vehiclerental.model.VehicleForm;
import com.vehiclerental.repository.VehicleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<Vehicle> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return vehicleRepository.findAll(Sort.by("registrationNumber"));
        }
        String searchTerm = keyword.trim();
        return vehicleRepository.findByRegistrationNumberContainingIgnoreCaseOrBrandContainingIgnoreCaseOrModelContainingIgnoreCaseOrderByRegistrationNumberAsc(
                searchTerm, searchTerm, searchTerm);
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    @Override
    public boolean isRegistrationTaken(String registrationNumber, Long excludeVehicleId) {
        if (registrationNumber == null || registrationNumber.isBlank()) {
            return false;
        }
        String registration = registrationNumber.trim();
        return excludeVehicleId == null
                ? vehicleRepository.existsByRegistrationNumberIgnoreCase(registration)
                : vehicleRepository.existsByRegistrationNumberIgnoreCaseAndVehicleIdNot(registration, excludeVehicleId);
    }

    @Override
    @Transactional
    public Vehicle create(VehicleForm form) {
        Vehicle vehicle = VehicleFactory.create(form.getVehicleType());
        copyFormToVehicle(form, vehicle);
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public Vehicle update(Long id, VehicleForm form) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found."));
        // Update the existing object: its Java subtype and discriminator cannot change.
        copyFormToVehicle(form, vehicle);
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
        // The shared bookings FK must use RESTRICT/NO ACTION, never ON DELETE SET NULL.
        vehicleRepository.flush();
    }

    private void copyFormToVehicle(VehicleForm form, Vehicle vehicle) {
        vehicle.setRegistrationNumber(form.getRegistrationNumber().trim().toUpperCase(Locale.ROOT));
        vehicle.setBrand(form.getBrand().trim());
        vehicle.setModel(form.getModel().trim());
        vehicle.setManufactureYear(form.getManufactureYear());
        vehicle.setSeatingCapacity(form.getSeatingCapacity());
        vehicle.setColour(form.getColour() == null ? null : form.getColour().trim());
        vehicle.setStatus(form.getStatus());
    }
}
