package com.vehiclerental.model;

/**
 * How much an admin is allowed to do.
 */
public enum AdminLevel {

    SUPER_ADMIN("Super admin"),
    MANAGER("Manager"),
    STAFF("Staff");

    private final String label;

    AdminLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
