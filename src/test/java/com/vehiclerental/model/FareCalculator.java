package com.vehiclerental.model;

import java.math.BigDecimal;

/**
 * Anything that can work out a fare from a distance.
 * Implemented by FareRule (normal fare) and NightFareCalculator (night surcharge).
 *
 * OOP: Abstraction (an interface says WHAT, not HOW) and Polymorphism
 *      (the same calculateFare() call gives different results for different objects).
 */
public interface FareCalculator {

    BigDecimal calculateFare(BigDecimal distanceKm);

    String getCalculatorName();
}
