package com.vehiclerental.model;

/**
 * Whether a driver can take a new trip right now.
 * Each value also knows its label and the Bootstrap colour used on the pages.
 */
public enum DriverAvailability {

    AVAILABLE("Available", "bg-success"),
    ON_TRIP("On a trip", "bg-warning text-dark"),
    OFF_DUTY("Off duty", "bg-secondary");

    private final String label;
    private final String badgeClass;

    DriverAvailability(String label, String badgeClass) {
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
