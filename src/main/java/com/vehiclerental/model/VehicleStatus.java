package com.vehiclerental.model;

public enum VehicleStatus {
    AVAILABLE("Available", "bg-success"),
    BOOKED("Booked", "bg-warning text-dark"),
    MAINTENANCE("Under maintenance", "bg-danger");

    private final String label;
    private final String badgeClass;

    VehicleStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
