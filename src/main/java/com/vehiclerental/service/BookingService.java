package com.vehiclerental.service;

import com.vehiclerental.model.Booking;
import com.vehiclerental.model.BookingOptionView;
import com.vehiclerental.model.BookingView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * What the Booking module can do.
 * OOP: Abstraction - BookingServiceImpl contains the details.
 */
public interface BookingService {

    List<BookingView> search(String status, String keyword);

    Optional<Booking> findById(Long id);

    List<BookingOptionView> getCustomerOptions();

    List<BookingOptionView> getVehicleOptions();

    List<BookingOptionView> getDriverOptions();

    /** Returns the fare, or null if there is no fare rule for the vehicle's type. */
    BigDecimal estimateFare(Long vehicleId, BigDecimal distanceKm, LocalDateTime pickupTime);

    PricingStrategy choosePricing(LocalDateTime pickupTime);

    Booking create(Booking booking);

    Booking update(Long id, Booking formData);

    void cancel(Long id);

    void delete(Long id);
}
