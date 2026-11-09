package com.vehiclerental.model;

/**
 * Rules for general feedback: usually LOW priority unless the rating is poor.
 */
public class GeneralFeedbackResponder extends FeedbackResponder {

    @Override
    public String calculatePriority(Feedback feedback) {
        return isLowRating(feedback) ? "MEDIUM" : "LOW";
    }

    @Override
    public String suggestResponse(Feedback feedback) {
        if (isLowRating(feedback)) {
            return "Thank you for your honest feedback. We will use it to improve our service.";
        }
        return "Thank you for your kind feedback! We look forward to serving you again.";
    }
}