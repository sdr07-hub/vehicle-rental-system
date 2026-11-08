package com.vehiclerental.model;

import java.math.BigDecimal;

/**
 * A read-only "view" of one booking, shown on the driver's trip page.
 * Spring fills these getters from the columns of the SQL query in DriverRepository
 * (each column name in the query matches a getter name below).
 */
public interface DriverTripView {

    Long getBookingId();

    String getCustomerName();

    String getCustomerPhone();

    String getPickupLocation();

    String getDropLocation();

    String getPickupTime();

    String getStatus();

    BigDecimal getEstimatedFare();
}
