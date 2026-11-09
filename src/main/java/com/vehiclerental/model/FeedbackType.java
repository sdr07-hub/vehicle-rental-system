package com.vehiclerental.model;

/**
 * The kind of message a customer sends.
 */
public enum FeedbackType {

    GENERAL("General feedback"),
    COMPLAINT("Complaint");

    private final String label;

    FeedbackType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}