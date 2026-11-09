package com.vehiclerental.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Normal pricing: the fare stays the same.
 */
public class StandardPricing implements PricingStrategy {

    @Override
    public BigDecimal apply(BigDecimal standardFare) {
        return standardFare.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getName() {
        return "Standard";
    }
}
