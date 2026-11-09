package com.vehiclerental.portal.model;

/**
 * The steps of a ride, stored as text in bookings.status:
 * PENDING -> CONFIRMED -> IN_PROGRESS -> COMPLETED
 * A ride can be CANCELLED while it is PENDING or CONFIRMED.
 * (Same values as Member 4's BookingStatus, so both modules read the same rows.)
 */
public enum RideStatus {

    PENDING("Waiting for a driver", "bg-secondary"),
    CONFIRMED("Driver on the way", "bg-primary"),
    IN_PROGRESS("On the trip", "bg-warning text-dark"),
    COMPLETED("Completed", "bg-success"),
    CANCELLED("Cancelled", "bg-danger");

    private final String label;
    private final String badgeClass;

    RideStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    /** Rule: which status is allowed to come next. */
    public boolean canMoveTo(RideStatus next) {
        return switch (this) {
            case PENDING -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == IN_PROGRESS || next == CANCELLED;
            case IN_PROGRESS -> next == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
