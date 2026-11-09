package com.vehiclerental.portal.service;

import com.vehiclerental.portal.model.RideStatus;
import com.vehiclerental.portal.model.RideView;
import com.vehiclerental.portal.repository.RideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business rules for drivers. Every status change is checked with RideStatus.canMoveTo(),
 * and a driver can only start or finish rides assigned to them.
 */
@Service
public class DriverRideServiceImpl implements DriverRideService {

    private final RideRepository rideRepository;

    public DriverRideServiceImpl(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @Override
    public List<RideView> getOpenRequests() {
        return rideRepository.findOpenRequests();
    }

    @Override
    public List<RideView> getMyRides(Long driverId) {
        return rideRepository.findRidesOfDriver(driverId);
    }

    @Override
    @Transactional
    public void acceptRide(Long driverId, Long bookingId) {
        if (!rideRepository.assignDriver(bookingId, driverId)) {
            throw new IllegalStateException("Ride #" + bookingId
                    + " was already accepted by another driver or cancelled by the customer.");
        }
    }

    @Override
    @Transactional
    public void startRide(Long driverId, Long bookingId) {
        moveOwnRide(driverId, bookingId, RideStatus.IN_PROGRESS);
    }

    @Override
    @Transactional
    public RideView finishRide(Long driverId, Long bookingId) {
        return moveOwnRide(driverId, bookingId, RideStatus.COMPLETED);
    }

    // ---------- helper ----------

    /** Loads the driver's own ride, checks the status rule, saves the new status and returns the updated ride. */
    private RideView moveOwnRide(Long driverId, Long bookingId, RideStatus next) {
        RideView ride = rideRepository.findRide(bookingId)
                .filter(r -> driverId.equals(r.getDriverId()))
                .orElseThrow(() -> new IllegalArgumentException("Ride #" + bookingId + " is not assigned to you."));

        RideStatus current = ride.getRideStatus();
        if (!current.canMoveTo(next)) {
            throw new IllegalStateException("Ride #" + bookingId + " is " + current.getLabel().toLowerCase()
                    + ", so it cannot be changed to " + next.getLabel().toLowerCase() + ".");
        }
        if (!rideRepository.changeStatus(bookingId, current, next)) {
            throw new IllegalStateException("Ride #" + bookingId + " just changed. Please refresh and try again.");
        }
        return rideRepository.findRide(bookingId).orElseThrow();
    }
}
