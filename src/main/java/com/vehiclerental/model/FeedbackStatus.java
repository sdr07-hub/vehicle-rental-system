package com.vehiclerental.model;

/**
 * Where a feedback item or complaint is in the review process.
 */
public enum FeedbackStatus {

    OPEN("Open", "bg-danger"),
    IN_REVIEW("In review", "bg-warning text-dark"),
    RESOLVED("Resolved", "bg-success"),
    CLOSED("Closed", "bg-secondary");

    private final String label;
    private final String badgeClass;

    FeedbackStatus(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    /** RESOLVED and CLOSED items are finished. */
    public boolean isFinished() {
        return this == RESOLVED || this == CLOSED;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}