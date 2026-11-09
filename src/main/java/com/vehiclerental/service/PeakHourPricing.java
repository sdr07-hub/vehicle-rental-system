package com.vehiclerental.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Busy-time pricing: 20% extra for pickups between 7-9 AM and 5-7 PM.
 * (The customer portal's RideFareCalculator uses the same hours and the same 20%.)
 */
public class PeakHourPricing implements PricingStrategy {

    private static final BigDecimal PEAK_MULTIPLIER = new BigDecimal("1.20");

    /** true if the pickup time falls inside a peak period. */
    public static boolean isPeakTime(LocalDateTime pickupTime) {
        int hour = pickupTime.getHour();
        return (hour >= 7 && hour < 9) || (hour >= 17 && hour < 19);
    }

    @Override
    public BigDecimal apply(BigDecimal standardFare) {
        return standardFare.multiply(PEAK_MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getName() {
        return "Peak hour (+20%)";
    }
}
