package com.vehiclerental.model;

/**
 * One choice in a drop-down list on the booking form (an ID and the text to show).
 */
public interface BookingOptionView {

    Long getId();

    String getLabel();
}
