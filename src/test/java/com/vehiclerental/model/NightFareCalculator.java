package com.vehiclerental.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Adds a 25% night surcharge on top of another fare calculator.
 * It "wraps" a normal FareRule: first the normal fare is worked out, then 25% is added.
 *
 * OOP: implements the same FareCalculator interface as FareRule,
 *      so the code using it does not care which one it has (Polymorphism).
 */
public class NightFareCalculator implements FareCalculator {

    private static final BigDecimal NIGHT_MULTIPLIER = new BigDecimal("1.25");

    private final FareCalculator normalCalculator;

    public NightFareCalculator(FareCalculator normalCalculator) {
        this.normalCalculator = normalCalculator;
    }

    @Override
    public BigDecimal calculateFare(BigDecimal distanceKm) {
        return normalCalculator.calculateFare(distanceKm)
                .multiply(NIGHT_MULTIPLIER)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getCalculatorName() {
        return normalCalculator.getCalculatorName() + " + 25% night surcharge";
    }
}
