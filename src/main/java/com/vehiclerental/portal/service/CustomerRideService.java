package com.vehiclerental.portal.service;

import com.vehiclerental.portal.model.RideRequestForm;
import com.vehiclerental.portal.model.RideView;
import com.vehiclerental.portal.model.VehicleTypeView;

import java.time.LocalDateTime;
import java.util.List;

/**
 * What a logged-in customer can do.
 * OOP: Abstraction - the controller only knows this interface, not CustomerRideServiceImpl.
 */
public interface CustomerRideService {

    List<VehicleTypeView> getVehicleTypes();

    List<RideView> getMyRides(Long customerId);

    /** Books a ride. Throws IllegalStateException with a friendly message if it cannot be booked. */
    RideView bookRide(Long customerId, RideRequestForm form);

    /** Cancels one of the customer's own rides (only while it is Pending or Confirmed). */
    void cancelRide(Long customerId, Long bookingId);

    /** "Standard" or "Peak hour (+20%)" for this pickup time. */
    String pricingName(LocalDateTime pickupTime);
}
