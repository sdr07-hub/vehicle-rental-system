package com.vehiclerental.portal.service;

import com.vehiclerental.portal.model.RideRequestForm;
import com.vehiclerental.portal.model.RideStatus;
import com.vehiclerental.portal.model.RideView;
import com.vehiclerental.portal.model.VehicleTypeView;
import com.vehiclerental.portal.pricing.RideFareCalculator;
import com.vehiclerental.portal.pricing.RideFareRule;
import com.vehiclerental.portal.repository.RideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business rules for customers: choose a free vehicle, work out the fare, save the ride, cancel a ride.
 */
@Service
public class CustomerRideServiceImpl implements CustomerRideService {

    private final RideRepository rideRepository;
    private final RideFareCalculator rideFareCalculator;

    public CustomerRideServiceImpl(RideRepository rideRepository, RideFareCalculator rideFareCalculator) {
        this.rideRepository = rideRepository;
        this.rideFareCalculator = rideFareCalculator;
    }

    @Override
    public List<VehicleTypeView> getVehicleTypes() {
        return rideRepository.findVehicleTypes();
    }

    @Override
    public List<RideView> getMyRides(Long customerId) {
        return rideRepository.findRidesOfCustomer(customerId);
    }

    @Override
    @Transactional
    public RideView bookRide(Long customerId, RideRequestForm form) {
        String type = form.getVehicleType();

        RideFareRule rule = rideRepository.findFareRule(type)
                .orElseThrow(() -> new IllegalStateException(
                        "This vehicle type has no price yet. Please choose another type."));

        Long vehicleId = rideRepository.findFreeVehicleId(type)
                .orElseThrow(() -> new IllegalStateException(
                        "Sorry, no " + type + " is available right now. Please choose another vehicle type."));

        BigDecimal fare = rideFareCalculator.calculate(rule, form.getDistanceKm(), form.getPickupTime());

        long bookingId = rideRepository.insertRide(customerId, vehicleId,
                form.getPickupLocation().trim(), form.getDropLocation().trim(),
                form.getPickupTime(), form.getDistanceKm(), fare);

        return rideRepository.findRide(bookingId).orElseThrow();
    }

    @Override
    @Transactional
    public void cancelRide(Long customerId, Long bookingId) {
        RideView ride = rideRepository.findRide(bookingId)
                .filter(r -> r.getCustomerId().equals(customerId)) // only your own rides
                .orElseThrow(() -> new IllegalArgumentException("Ride not found."));

        RideStatus current = ride.getRideStatus();
        if (!current.canMoveTo(RideStatus.CANCELLED)) {
            throw new IllegalStateException("Ride #" + bookingId + " is " + current.getLabel().toLowerCase()
                    + ", so it can no longer be cancelled.");
        }
        if (!rideRepository.changeStatus(bookingId, current, RideStatus.CANCELLED)) {
            throw new IllegalStateException("Ride #" + bookingId + " just changed. Please refresh and try again.");
        }
    }

    @Override
    public String pricingName(LocalDateTime pickupTime) {
        return rideFareCalculator.pricingName(pickupTime);
    }
}
