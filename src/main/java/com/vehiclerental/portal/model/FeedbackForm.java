package com.vehiclerental.portal.model;

import com.vehiclerental.model.FeedbackType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * What a logged-in customer types on the "Give feedback" page.
 * There is NO customerId field on purpose: the customer always comes from the session,
 * so nobody can send feedback in someone else's name.
 */
public class FeedbackForm {

    @NotNull(message = "Please choose general feedback or complaint")
    private FeedbackType feedbackType = FeedbackType.GENERAL;

    @NotNull(message = "Please choose a rating")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating = 5;

    @NotBlank(message = "Subject is required")
    @Size(max = 150, message = "Subject must be 150 characters or less")
    private String subject;

    @NotBlank(message = "Please write your message")
    @Size(max = 1000, message = "Message must be 1000 characters or less")
    private String message;

    // Optional: the ride this feedback is about
    private Long bookingId;

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
}
