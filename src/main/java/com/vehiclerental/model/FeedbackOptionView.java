package com.vehiclerental.model;

/**
 * One choice in a drop-down list on the feedback form (an ID and the text to show).
 */
public interface FeedbackOptionView {

    Long getId();

    String getLabel();
}