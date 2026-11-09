package com.vehiclerental.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * What the customer types on the "Book a ride" page.
 * The customer chooses a vehicle TYPE; the system picks a free vehicle of that type.
 * Not saved to the database itself - the service turns it into a Booking.
 */
public class RideRequestForm {

    @NotBlank(message = "Please choose a vehicle type")
    private String vehicleType;

    @NotBlank(message = "Pickup location is required")
    @Size(max = 150, message = "Pickup location must be 150 characters or less")
    private String pickupLocation;

    @NotBlank(message = "Drop location is required")
    @Size(max = 150, message = "Drop location must be 150 characters or less")
    private String dropLocation;

    @NotNull(message = "Pickup date and time are required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime pickupTime;

    @NotNull(message = "Distance is required")
    @DecimalMin(value = "0.5", message = "Distance must be at least 0.5 km")
    @DecimalMax(value = "1000", message = "Distance must be 1000 km or less")
    private BigDecimal distanceKm;

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
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
}