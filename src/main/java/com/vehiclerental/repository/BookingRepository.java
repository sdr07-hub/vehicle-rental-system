package com.vehiclerental.repository;

import com.vehiclerental.model.Booking;
import com.vehiclerental.model.BookingOptionView;
import com.vehiclerental.model.BookingView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Database access for bookings.
 * The SQL queries read the other tables (users, vehicles, fare_rules) directly,
 * so this module works on its own branch without the other members' classes.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Booking list with customer/driver names. Empty status or keyword means "show all".
    @Query(value = """
            SELECT b.booking_id      AS bookingId,
                   cu.full_name      AS customerName,
                   CONCAT(v.vehicle_type, ' - ', v.registration_number) AS vehicleLabel,
                   du.full_name      AS driverName,
                   b.pickup_location AS pickupLocation,
                   b.drop_location   AS dropLocation,
                   DATE_FORMAT(b.pickup_time, '%Y-%m-%d %H:%i') AS pickupTime,
                   b.distance_km     AS distanceKm,
                   b.estimated_fare  AS estimatedFare,
                   b.status          AS status
            FROM bookings b
            JOIN users cu    ON cu.user_id = b.customer_id
            JOIN vehicles v  ON v.vehicle_id = b.vehicle_id
            LEFT JOIN users du ON du.user_id = b.driver_id
            WHERE (:status = '' OR b.status = :status)
              AND (:keyword = ''
                   OR cu.full_name       LIKE CONCAT('%', :keyword, '%')
                   OR b.pickup_location  LIKE CONCAT('%', :keyword, '%')
                   OR b.drop_location    LIKE CONCAT('%', :keyword, '%'))
            ORDER BY b.pickup_time DESC
            """, nativeQuery = true)
    List<BookingView> findBookingList(@Param("status") String status, @Param("keyword") String keyword);

    // Drop-down: active customers
    @Query(value = """
            SELECT c.customer_id AS id,
                   CONCAT(u.full_name, ' (', u.phone, ')') AS label
            FROM customers c
            JOIN users u ON u.user_id = c.customer_id
            WHERE u.active = TRUE
            ORDER BY u.full_name
            """, nativeQuery = true)
    List<BookingOptionView> findCustomerOptions();

    // Drop-down: vehicles that are not under maintenance
    @Query(value = """
            SELECT vehicle_id AS id,
                   CONCAT(vehicle_type, ' - ', registration_number, ' - ', brand, ' ', model,
                          ' (', seating_capacity, ' seats)') AS label
            FROM vehicles
            WHERE status <> 'MAINTENANCE'
            ORDER BY vehicle_type, registration_number
            """, nativeQuery = true)
    List<BookingOptionView> findVehicleOptions();

    // Drop-down: active drivers with a licence that has not expired
    @Query(value = """
            SELECT d.driver_id AS id,
                   CONCAT(u.full_name, ' - ', d.availability) AS label
            FROM drivers d
            JOIN users u ON u.user_id = d.driver_id
            WHERE u.active = TRUE AND d.licence_expiry >= CURDATE()
            ORDER BY u.full_name
            """, nativeQuery = true)
    List<BookingOptionView> findDriverOptions();

    // Standard fare = base fare + (price per km x distance), but never less than the minimum fare.
    // Returns null if there is no active fare rule for this vehicle's type.
    @Query(value = """
            SELECT GREATEST(fr.base_fare + fr.price_per_km * :distanceKm, fr.minimum_fare)
            FROM fare_rules fr
            JOIN vehicles v ON v.vehicle_type = fr.vehicle_type
            WHERE v.vehicle_id = :vehicleId AND fr.active = TRUE
            """, nativeQuery = true)
    BigDecimal calculateStandardFare(@Param("vehicleId") Long vehicleId,
                                     @Param("distanceKm") BigDecimal distanceKm);
}
