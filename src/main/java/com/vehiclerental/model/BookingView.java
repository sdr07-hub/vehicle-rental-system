package com.vehiclerental.model;

import java.math.BigDecimal;

/**
 * One row of the booking list, with names instead of ID numbers.
 * Filled from the SQL query in BookingRepository.findBookingList(...).
 */
public interface BookingView {

    Long getBookingId();

    String getCustomerName();

    String getVehicleLabel();

    String getDriverName();

    String getPickupLocation();

    String getDropLocation();

    String getPickupTime();

    BigDecimal getDistanceKm();

    BigDecimal getEstimatedFare();

    String getStatus();
}
