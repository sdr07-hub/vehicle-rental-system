package com.vehiclerental.service;

import java.math.BigDecimal;

/**
 * A way of turning the normal fare into the final booking price.
 * Two classes implement it: StandardPricing and PeakHourPricing.
 *
 * OOP:
 *  - Abstraction: the booking service only knows this interface.
 *  - Polymorphism: calling apply() runs a different version depending on which object we have.
 */
public interface PricingStrategy {

    /** Returns the final fare, starting from the standard fare. */
    BigDecimal apply(BigDecimal standardFare);

    /** A short name shown to the user, for example "Peak hour (+20%)". */
    String getName();
}
