package com.vehiclerental.repository;

import com.vehiclerental.model.Vehicle;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByRegistrationNumberContainingIgnoreCaseOrBrandContainingIgnoreCaseOrModelContainingIgnoreCaseOrderByRegistrationNumberAsc(
            String registrationNumber, String brand, String model);

    boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);

    boolean existsByRegistrationNumberIgnoreCaseAndVehicleIdNot(String registrationNumber, Long vehicleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select vehicle from Vehicle vehicle where vehicle.vehicleId = :id")
    Optional<Vehicle> findByIdForUpdate(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM bookings WHERE vehicle_id = :id", nativeQuery = true)
    long countBookingReferences(@Param("id") Long id);

    @Query(value = """
            SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'bookings' AND COLUMN_NAME = 'vehicle_id'
              AND REFERENCED_TABLE_SCHEMA = DATABASE() AND REFERENCED_TABLE_NAME = 'vehicles'
              AND REFERENCED_COLUMN_NAME = 'vehicle_id'
            """, nativeQuery = true)
    long countBookingVehicleForeignKeys();
}
