package com.vehiclerental.model;

/**
 * Base class for feedback response rules.
 *
 * - Abstraction: abstract class with abstract methods.
 * - Inheritance: ComplaintResponder and GeneralFeedbackResponder extend this class.
 * - Polymorphism: forType() returns one of the children, and calling its methods runs that child's version.
 */
public abstract class FeedbackResponder {

    /** "HIGH", "MEDIUM" or "LOW". */
    public abstract String calculatePriority(Feedback feedback);

    /** A ready-made reply the admin can edit before saving. */
    public abstract String suggestResponse(Feedback feedback);

    /** Shared helper used by both child classes. */
    protected boolean isLowRating(Feedback feedback) {
        return feedback.getRating() != null && feedback.getRating() <= 2;
    }

    /** Factory method: gives the right responder for the feedback type. */
    public static FeedbackResponder forType(FeedbackType type) {
        if (type == FeedbackType.COMPLAINT) {
            return new ComplaintResponder();
        }
        return new GeneralFeedbackResponder();
    }
}