package com.vehiclerental.model;

import java.util.Locale;

public final class VehicleFactory {

    public static final String[] TYPE_CODES = {"CAR", "VAN", "SUV"};

    private VehicleFactory() {
    }

    public static Vehicle create(String typeCode) {
        if (typeCode == null) {
            throw new IllegalArgumentException("Please choose a vehicle type");
        }
        return switch (typeCode.toUpperCase(Locale.ROOT)) {
            case "CAR" -> new Car();
            case "VAN" -> new Van();
            case "SUV" -> new Suv();
            default -> throw new IllegalArgumentException("Invalid vehicle type: " + typeCode);
        };
    }

    public static boolean isValidType(String typeCode) {
        return typeCode != null && switch (typeCode.toUpperCase(Locale.ROOT)) {
            case "CAR", "VAN", "SUV" -> true;
            default -> false;
        };
    }
}
