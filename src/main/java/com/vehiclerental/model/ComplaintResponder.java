package com.vehiclerental.model;

/**
 * Rules for complaints: they are always at least MEDIUM priority.
 */
public class ComplaintResponder extends FeedbackResponder {

    @Override
    public String calculatePriority(Feedback feedback) {
        return isLowRating(feedback) ? "HIGH" : "MEDIUM";
    }

    @Override
    public String suggestResponse(Feedback feedback) {
        return "We are sorry about your experience regarding \"" + feedback.getSubject()
                + "\". Our team has investigated the issue and taken action to prevent it from happening again.";
    }
}