package com.vehiclerental.portal.model;

import java.math.BigDecimal;

/**
 * One ride as shown on the customer and driver pages (names instead of ID numbers).
 * Filled by RideRepository from a SQL query. Read-only: there are no setters.
 *
 * OOP: Encapsulation - private final fields, getters only.
 */
public class RideView {

    private final Long bookingId;
    private final Long customerId;
    private final Long driverId;          // null until a driver accepts
    private final String customerName;
    private final String driverName;      // null until a driver accepts
    private final String vehicleLabel;
    private final String pickupLocation;
    private final String dropLocation;
    private final String pickupTime;      // already formatted, e.g. 2026-11-10 09:30
    private final BigDecimal distanceKm;
    private final BigDecimal estimatedFare;
    private final String status;          // PENDING, CONFIRMED, IN_PROGRESS, COMPLETED or CANCELLED

    public RideView(Long bookingId, Long customerId, Long driverId, String customerName, String driverName,
                    String vehicleLabel, String pickupLocation, String dropLocation, String pickupTime,
                    BigDecimal distanceKm, BigDecimal estimatedFare, String status) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.driverId = driverId;
        this.customerName = customerName;
        this.driverName = driverName;
        this.vehicleLabel = vehicleLabel;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.pickupTime = pickupTime;
        this.distanceKm = distanceKm;
        this.estimatedFare = estimatedFare;
        this.status = status;
    }

    /** The status as an enum, so the rules in RideStatus can be used. */
    public RideStatus getRideStatus() {
        return RideStatus.valueOf(status);
    }

    /** true while the ride still needs something to happen (not finished, not cancelled). */
    public boolean isActive() {
        return "PENDING".equals(status) || "CONFIRMED".equals(status) || "IN_PROGRESS".equals(status);
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getVehicleLabel() {
        return vehicleLabel;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public BigDecimal getEstimatedFare() {
        return estimatedFare;
    }

    public String getStatus() {
        return status;
    }
}
