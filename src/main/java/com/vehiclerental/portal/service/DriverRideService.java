package com.vehiclerental.portal.service;

import com.vehiclerental.portal.model.RideView;

import java.util.List;

/**
 * What a logged-in driver can do with rides.
 * OOP: Abstraction - the details are in DriverRideServiceImpl.
 */
public interface DriverRideService {

    List<RideView> getOpenRequests();

    List<RideView> getMyRides(Long driverId);

    /** PENDING -> CONFIRMED, and the ride now belongs to this driver. */
    void acceptRide(Long driverId, Long bookingId);

    /** CONFIRMED -> IN_PROGRESS (customer picked up). */
    void startRide(Long driverId, Long bookingId);

    /** IN_PROGRESS -> COMPLETED. Returns the finished ride so the total can be shown. */
    RideView finishRide(Long driverId, Long bookingId);
}
