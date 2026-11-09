package com.vehiclerental.portal.repository;

import com.vehiclerental.portal.model.RideStatus;
import com.vehiclerental.portal.model.RideView;
import com.vehiclerental.portal.model.VehicleTypeView;
import com.vehiclerental.portal.pricing.RideFareRule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * All database work for the customer and driver portal, written with JdbcTemplate (plain SQL).
 * It reads and writes the shared tables (bookings, vehicles, fare_rules, users) directly,
 * so this module does not need any other member's Java classes.
 */
@Repository
public class RideRepository {

    // The columns every ride query returns. Names instead of ID numbers for the pages.
    private static final String RIDE_SELECT = """
            SELECT b.booking_id, b.customer_id, b.driver_id,
                   cu.full_name AS customer_name,
                   du.full_name AS driver_name,
                   CONCAT(v.vehicle_type, ' - ', v.brand, ' ', v.model, ' (', v.registration_number, ')') AS vehicle_label,
                   b.pickup_location, b.drop_location,
                   DATE_FORMAT(b.pickup_time, '%Y-%m-%d %H:%i') AS pickup_time,
                   b.distance_km, b.estimated_fare, b.status
            FROM bookings b
            JOIN users cu      ON cu.user_id = b.customer_id
            JOIN vehicles v    ON v.vehicle_id = b.vehicle_id
            LEFT JOIN users du ON du.user_id = b.driver_id
            """;

    // Turns one SQL result row into one RideView object
    private static final RowMapper<RideView> RIDE_MAPPER = (rs, rowNum) -> new RideView(
            rs.getLong("booking_id"),
            rs.getLong("customer_id"),
            rs.getObject("driver_id", Long.class),   // stays null when no driver yet
            rs.getString("customer_name"),
            rs.getString("driver_name"),
            rs.getString("vehicle_label"),
            rs.getString("pickup_location"),
            rs.getString("drop_location"),
            rs.getString("pickup_time"),
            rs.getBigDecimal("distance_km"),
            rs.getBigDecimal("estimated_fare"),
            rs.getString("status"));

    private final JdbcTemplate jdbc;

    public RideRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------- vehicle types and prices ----------

    /** One row per vehicle type that has an active fare rule, cheapest first. */
    public List<VehicleTypeView> findVehicleTypes() {
        String sql = """
                SELECT fr.vehicle_type, fr.base_fare, fr.price_per_km, fr.minimum_fare,
                       COALESCE(MAX(v.seating_capacity), 0) AS seats,
                       COUNT(CASE WHEN v.status <> 'MAINTENANCE' THEN 1 END) AS available_count
                FROM fare_rules fr
                LEFT JOIN vehicles v ON v.vehicle_type = fr.vehicle_type
                WHERE fr.active = TRUE
                GROUP BY fr.vehicle_type, fr.base_fare, fr.price_per_km, fr.minimum_fare
                ORDER BY fr.base_fare
                """;
        return jdbc.query(sql, (rs, rowNum) -> new VehicleTypeView(
                rs.getString("vehicle_type"),
                rs.getBigDecimal("base_fare"),
                rs.getBigDecimal("price_per_km"),
                rs.getBigDecimal("minimum_fare"),
                rs.getInt("seats"),
                rs.getInt("available_count")));
    }

    /** The active price settings for a vehicle type, if there are any. */
    public Optional<RideFareRule> findFareRule(String vehicleType) {
        String sql = """
                SELECT base_fare, price_per_km, minimum_fare
                FROM fare_rules
                WHERE vehicle_type = ? AND active = TRUE
                """;
        return jdbc.query(sql, (rs, rowNum) -> new RideFareRule(
                        rs.getBigDecimal("base_fare"),
                        rs.getBigDecimal("price_per_km"),
                        rs.getBigDecimal("minimum_fare")), vehicleType)
                .stream().findFirst();
    }

    /** The vehicle of this type with the fewest open rides (not under maintenance). */
    public Optional<Long> findFreeVehicleId(String vehicleType) {
        String sql = """
                SELECT v.vehicle_id
                FROM vehicles v
                WHERE v.vehicle_type = ? AND v.status <> 'MAINTENANCE'
                ORDER BY (SELECT COUNT(*) FROM bookings b
                          WHERE b.vehicle_id = v.vehicle_id
                            AND b.status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS')),
                         v.vehicle_id
                LIMIT 1
                """;
        return jdbc.queryForList(sql, Long.class, vehicleType).stream().findFirst();
    }

    // ---------- saving and changing rides ----------

    /** Saves a new ride as PENDING (no driver yet) and returns its new booking_id. */
    public long insertRide(Long customerId, Long vehicleId, String pickupLocation, String dropLocation,
                           LocalDateTime pickupTime, BigDecimal distanceKm, BigDecimal fare) {
        String sql = """
                INSERT INTO bookings (customer_id, vehicle_id, driver_id, pickup_location, drop_location,
                                      pickup_time, distance_km, estimated_fare, status)
                VALUES (?, ?, NULL, ?, ?, ?, ?, ?, 'PENDING')
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, customerId);
            ps.setLong(2, vehicleId);
            ps.setString(3, pickupLocation);
            ps.setString(4, dropLocation);
            ps.setTimestamp(5, Timestamp.valueOf(pickupTime));
            ps.setBigDecimal(6, distanceKm);
            ps.setBigDecimal(7, fare);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    /**
     * Gives a PENDING ride to a driver. The WHERE part makes sure only ONE driver can win,
     * even if two drivers press Accept at the same moment. Returns false if someone was faster.
     */
    public boolean assignDriver(Long bookingId, Long driverId) {
        String sql = """
                UPDATE bookings
                SET driver_id = ?, status = 'CONFIRMED'
                WHERE booking_id = ? AND driver_id IS NULL AND status = 'PENDING'
                """;
        return jdbc.update(sql, driverId, bookingId) == 1;
    }

    /** Changes the status only if it is still what we expect. Returns false if it changed meanwhile. */
    public boolean changeStatus(Long bookingId, RideStatus from, RideStatus to) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ? AND status = ?";
        return jdbc.update(sql, to.name(), bookingId, from.name()) == 1;
    }

    // ---------- reading rides ----------

    public Optional<RideView> findRide(Long bookingId) {
        return jdbc.query(RIDE_SELECT + " WHERE b.booking_id = ?", RIDE_MAPPER, bookingId)
                .stream().findFirst();
    }

    /** All rides of one customer, newest first. */
    public List<RideView> findRidesOfCustomer(Long customerId) {
        return jdbc.query(RIDE_SELECT + " WHERE b.customer_id = ? ORDER BY b.pickup_time DESC",
                RIDE_MAPPER, customerId);
    }

    /** Rides no driver has accepted yet, soonest first. */
    public List<RideView> findOpenRequests() {
        return jdbc.query(RIDE_SELECT + " WHERE b.status = 'PENDING' AND b.driver_id IS NULL ORDER BY b.pickup_time",
                RIDE_MAPPER);
    }

    /** Rides of one driver: active ones first, then finished ones. */
    public List<RideView> findRidesOfDriver(Long driverId) {
        return jdbc.query(RIDE_SELECT + """
                 WHERE b.driver_id = ?
                 ORDER BY FIELD(b.status, 'IN_PROGRESS', 'CONFIRMED', 'PENDING', 'COMPLETED', 'CANCELLED'),
                          b.pickup_time DESC
                """, RIDE_MAPPER, driverId);
    }
}
