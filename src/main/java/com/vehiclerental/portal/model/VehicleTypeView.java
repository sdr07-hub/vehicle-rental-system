package com.vehiclerental.portal.model;

import java.math.BigDecimal;

/**
 * One vehicle type card on the customer pages (CAR, VAN or SUV) with its prices.
 * Filled by RideRepository.findVehicleTypes() from the fare_rules and vehicles tables.
 */
public class VehicleTypeView {

    private final String vehicleType;
    private final BigDecimal baseFare;
    private final BigDecimal pricePerKm;
    private final BigDecimal minimumFare;
    private final int seats;           // largest seating capacity of this type
    private final int availableCount;  // vehicles of this type that are not under maintenance

    public VehicleTypeView(String vehicleType, BigDecimal baseFare, BigDecimal pricePerKm,
                           BigDecimal minimumFare, int seats, int availableCount) {
        this.vehicleType = vehicleType;
        this.baseFare = baseFare;
        this.pricePerKm = pricePerKm;
        this.minimumFare = minimumFare;
        this.seats = seats;
        this.availableCount = availableCount;
    }

    public String getDisplayName() {
        return switch (vehicleType) {
            case "CAR" -> "Car";
            case "VAN" -> "Van";
            case "SUV" -> "SUV";
            default -> vehicleType;
        };
    }

    public String getTagline() {
        return switch (vehicleType) {
            case "CAR" -> "Everyday rides for up to 4 people";
            case "VAN" -> "Groups, families and airport trips";
            case "SUV" -> "Comfort and space for long journeys";
            default -> "Comfortable ride";
        };
    }

    /** Picture file in src/main/resources/static/images, for example car.jpg */
    public String getImageFile() {
        return vehicleType.toLowerCase() + ".jpg";
    }

    public boolean isBookable() {
        return availableCount > 0;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public BigDecimal getPricePerKm() {
        return pricePerKm;
    }

    public BigDecimal getMinimumFare() {
        return minimumFare;
    }

    public int getSeats() {
        return seats;
    }

    public int getAvailableCount() {
        return availableCount;
    }
}
