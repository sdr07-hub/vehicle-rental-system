package com.vehiclerental.portal.pricing;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Works out the fare of a ride:
 *   standard fare = base fare + price per km x distance, but never less than the minimum fare
 *   peak hours (7-9 AM and 5-7 PM) add 20%
 * The book-a-ride page uses the same formula in JavaScript to show the estimate while the customer types.
 */
@Component
public class RideFareCalculator {

    private static final BigDecimal PEAK_MULTIPLIER = new BigDecimal("1.20");

    /** true if the pickup time falls inside a peak period. */
    public boolean isPeakTime(LocalDateTime pickupTime) {
        int hour = pickupTime.getHour();
        return (hour >= 7 && hour < 9) || (hour >= 17 && hour < 19);
    }

    public BigDecimal calculate(RideFareRule rule, BigDecimal distanceKm, LocalDateTime pickupTime) {
        BigDecimal fare = rule.baseFare().add(rule.pricePerKm().multiply(distanceKm));
        if (fare.compareTo(rule.minimumFare()) < 0) {
            fare = rule.minimumFare();
        }
        if (isPeakTime(pickupTime)) {
            fare = fare.multiply(PEAK_MULTIPLIER);
        }
        return fare.setScale(2, RoundingMode.HALF_UP);
    }

    /** Short text shown to the customer, for example "Peak hour (+20%)". */
    public String pricingName(LocalDateTime pickupTime) {
        return isPeakTime(pickupTime) ? "Peak hour (+20%)" : "Standard";
    }
}
