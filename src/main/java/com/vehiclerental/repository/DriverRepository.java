package com.vehiclerental.repository;

import com.vehiclerental.model.Driver;
import com.vehiclerental.model.DriverTripView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Database access for drivers.
 */
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Search by name, email or licence number
    List<Driver> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrLicenceNumberContainingIgnoreCaseOrderByFullNameAsc(
            String fullName, String email, String licenceNumber);

    boolean existsByLicenceNumberIgnoreCase(String licenceNumber);

    boolean existsByLicenceNumberIgnoreCaseAndUserIdNot(String licenceNumber, Long userId);

    // Trips (bookings) assigned to one driver - plain SQL that reads the bookings and users tables
    @Query(value = """
            SELECT b.booking_id      AS bookingId,
                   u.full_name       AS customerName,
                   u.phone           AS customerPhone,
                   b.pickup_location AS pickupLocation,
                   b.drop_location   AS dropLocation,
                   DATE_FORMAT(b.pickup_time, '%Y-%m-%d %H:%i') AS pickupTime,
                   b.status          AS status,
                   b.estimated_fare  AS estimatedFare
            FROM bookings b
            JOIN users u ON u.user_id = b.customer_id
            WHERE b.driver_id = :driverId
            ORDER BY b.pickup_time DESC
            """, nativeQuery = true)
    List<DriverTripView> findTripsForDriver(@Param("driverId") Long driverId);
}
