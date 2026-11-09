package com.vehiclerental.model;

import java.math.BigDecimal;

/**
 * One vehicle type card on the customer dashboard (CAR, VAN or SUV).
 * Filled from the SQL query in PortalRepository.findVehicleTypes().
 * The default methods add display text, so the JSP page stays simple.
 */
public interface VehicleTypeView {

    String getVehicleType();

    BigDecimal getBaseFare();

    BigDecimal getPricePerKm();

    BigDecimal getMinimumFare();

    Number getSeats();          // largest seating capacity of this type

    Number getAvailableCount(); // vehicles of this type that are not under maintenance

    default String getDisplayName() {
        return switch (getVehicleType()) {
            case "CAR" -> "Car";
            case "VAN" -> "Van";
            case "SUV" -> "SUV";
            default -> getVehicleType();
        };
    }

    default String getTagline() {
        return switch (getVehicleType()) {
            case "CAR" -> "Everyday rides for up to 4 people";
            case "VAN" -> "Groups, families and airport trips";
            case "SUV" -> "Comfort and space for long journeys";
            default -> "Comfortable ride";
        };
    }

    /** Picture file in src/main/resources/static/images, for example car.jpg */
    default String getImageFile() {
        return getVehicleType().toLowerCase() + ".jpg";
    }

    default boolean isBookable() {
        return getAvailableCount() != null && getAvailableCount().intValue() > 0;
    }
}