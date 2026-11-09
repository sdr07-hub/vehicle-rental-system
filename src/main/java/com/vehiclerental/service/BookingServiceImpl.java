package com.vehiclerental.service;

import com.vehiclerental.model.Booking;
import com.vehiclerental.model.BookingOptionView;
import com.vehiclerental.model.BookingStatus;
import com.vehiclerental.model.BookingView;
import com.vehiclerental.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Business rules and database calls for bookings.
 */
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<BookingView> search(String status, String keyword) {
        // The SQL treats '' (empty text) as "no filter"
        String s = (status == null) ? "" : status.trim();
        String k = (keyword == null) ? "" : keyword.trim();
        return bookingRepository.findBookingList(s, k);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<BookingOptionView> getCustomerOptions() {
        return bookingRepository.findCustomerOptions();
    }

    @Override
    public List<BookingOptionView> getVehicleOptions() {
        return bookingRepository.findVehicleOptions();
    }

    @Override
    public List<BookingOptionView> getDriverOptions() {
        return bookingRepository.findDriverOptions();
    }

    @Override
    public PricingStrategy choosePricing(LocalDateTime pickupTime) {
        // POLYMORPHISM: both objects are "PricingStrategy", but apply() works differently
        if (pickupTime != null && PeakHourPricing.isPeakTime(pickupTime)) {
            return new PeakHourPricing();
        }
        return new StandardPricing();
    }

    @Override
    public BigDecimal estimateFare(Long vehicleId, BigDecimal distanceKm, LocalDateTime pickupTime) {
        BigDecimal standardFare = bookingRepository.calculateStandardFare(vehicleId, distanceKm);
        if (standardFare == null) {
            return null;
        }
        PricingStrategy pricing = choosePricing(pickupTime);
        return pricing.apply(standardFare);
    }

    @Override
    @Transactional
    public Booking create(Booking booking) {
        booking.setEstimatedFare(estimateFare(booking.getVehicleId(), booking.getDistanceKm(), booking.getPickupTime()));
        // A booking with a driver is confirmed straight away; otherwise it waits as PENDING
        booking.setStatus(booking.isDriverAssigned() ? BookingStatus.CONFIRMED : BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking update(Long id, Booking formData) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));

        existing.setCustomerId(formData.getCustomerId());
        existing.setVehicleId(formData.getVehicleId());
        existing.setDriverId(formData.getDriverId());
        existing.setPickupLocation(formData.getPickupLocation());
        existing.setDropLocation(formData.getDropLocation());
        existing.setPickupTime(formData.getPickupTime());
        existing.setDistanceKm(formData.getDistanceKm());
        existing.setEstimatedFare(estimateFare(formData.getVehicleId(), formData.getDistanceKm(), formData.getPickupTime()));

        // changeStatus() checks the rule and throws an error if the move is not allowed
        existing.changeStatus(formData.getStatus());

        // AMENDMENT (new interface): a PENDING booking has no driver. This puts it back in the
        // drivers' "New ride requests" list, so any driver can accept it in the driver dashboard.
        if (existing.getStatus() == BookingStatus.PENDING) {
            existing.setDriverId(null);
        }
        return bookingRepository.save(existing);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + id));
        booking.cancel();
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        bookingRepository.deleteById(id);
        bookingRepository.flush();
    }
}
