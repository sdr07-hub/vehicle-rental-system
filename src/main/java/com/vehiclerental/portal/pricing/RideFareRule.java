package com.vehiclerental.portal.pricing;

import java.math.BigDecimal;

/**
 * The price settings of one vehicle type, read from the fare_rules table (managed by the admin module).
 * A Java record: a small read-only class whose fields, constructor and getters are written for us.
 */
public record RideFareRule(BigDecimal baseFare, BigDecimal pricePerKm, BigDecimal minimumFare) {
}
