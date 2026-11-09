package com.vehiclerental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A trip booked by a customer.
 * The customer, vehicle and driver are stored as ID numbers (foreign keys),
 * so this module does not depend on the other members' Java classes.
 *
 * OOP: Inheritance (extends BaseEntity), Encapsulation (status can only change through
 *      changeStatus()/cancel(), which check the rules first).
 */
@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @NotNull(message = "Please choose a customer")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @NotNull(message = "Please choose a vehicle")
    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    // Optional: a driver can be assigned later
    @Column(name = "driver_id")
    private Long driverId;

    @NotBlank(message = "Pickup location is required")
    @Size(max = 150, message = "Pickup location must be 150 characters or less")
    @Column(name = "pickup_location", nullable = false, length = 150)
    private String pickupLocation;

    @NotBlank(message = "Drop location is required")
    @Size(max = 150, message = "Drop location must be 150 characters or less")
    @Column(name = "drop_location", nullable = false, length = 150)
    private String dropLocation;

    @NotNull(message = "Pickup date and time are required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "pickup_time", nullable = false)
    private LocalDateTime pickupTime;

    @NotNull(message = "Distance is required")
    @DecimalMin(value = "0.5", message = "Distance must be at least 0.5 km")
    @DecimalMax(value = "1000", message = "Distance must be 1000 km or less")
    @Column(name = "distance_km", nullable = false, precision = 8, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "estimated_fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedFare = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    // ----- Rules kept inside the class (Encapsulation) -----

    /** Changes the status only if the move is allowed (for example COMPLETED -> PENDING is not). */
    public void changeStatus(BookingStatus next) {
        if (!status.canMoveTo(next)) {
            throw new IllegalStateException("Cannot change the booking from " + status.getLabel() + " to " + next.getLabel());
        }
        this.status = next;
    }

    public void cancel() {
        changeStatus(BookingStatus.CANCELLED);
    }

    public boolean isCancellable() {
        return status.canMoveTo(BookingStatus.CANCELLED) && status != BookingStatus.CANCELLED;
    }

    public boolean isDriverAssigned() {
        return driverId != null;
    }

    // ----- Getters and setters -----

    public Long getBookingId() {
        return bookingId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public LocalDateTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public BigDecimal getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(BigDecimal estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public BookingStatus getStatus() {
        return status;
    }

    // Used only by Spring when reading the edit form. Business code should call changeStatus().
    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking{id=" + bookingId + ", from='" + pickupLocation + "', to='" + dropLocation + "', status=" + status + "}";
    }
}
