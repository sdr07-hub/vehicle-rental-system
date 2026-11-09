package com.vehiclerental.model;

/**
 * The steps a booking goes through:
 * PENDING -> CONFIRMED -> IN_PROGRESS -> COMPLETED
 * A booking can be CANCELLED while it is PENDING or CONFIRMED.
 * (The same text values are used by the customer/driver portal, so both read the same rows.)
 */
public enum BookingStatus {

    PENDING("Pending", "bg-secondary"),
    CONFIRMED("Confirmed", "bg-primary"),
    IN_PROGRESS("In progress", "bg-warning text-dark"),
    COMPLETED("Completed", "bg-success"),
    CANCELLED("Cancelled", "bg-danger");

    private final String label;
    private final String badgeClass;

    BookingStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    /** Rule: which status is allowed to come next. */
    public boolean canMoveTo(BookingStatus next) {
        if (next == this) {
            return true; // no change is always fine
        }
        return switch (this) {
            case PENDING -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == IN_PROGRESS || next == CANCELLED || next == PENDING;
            case IN_PROGRESS -> next == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    /** A finished booking cannot be changed any more. */
    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
