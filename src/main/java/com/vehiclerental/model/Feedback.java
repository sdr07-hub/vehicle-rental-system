package com.vehiclerental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A rating, general feedback or complaint sent by a customer.
 * The customer and booking are stored as ID numbers (foreign keys).
 *
 * OOP: Inheritance (extends BaseEntity), Encapsulation (private fields),
 *    Polymorphism (getPriority() asks a FeedbackResponder, which is a different class for complaints).
 */
@Entity
@Table(name = "feedback")
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;

    @NotNull(message = "Please choose a customer")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    // Optional: the booking this feedback is about
    @Column(name = "booking_id")
    private Long bookingId;

    @NotNull(message = "Please choose the feedback type")
    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false, length = 20)
    private FeedbackType feedbackType = FeedbackType.GENERAL;

    @NotNull(message = "Please give a rating")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @NotBlank(message = "Subject is required")
    @Size(max = 150, message = "Subject must be 150 characters or less")
    @Column(name = "subject", nullable = false, length = 150)
    private String subject;

    @NotBlank(message = "Message is required")
    @Size(max = 1000, message = "Message must be 1000 characters or less")
    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FeedbackStatus status = FeedbackStatus.OPEN;

    @Size(max = 1000, message = "Response must be 1000 characters or less")
    @Column(name = "admin_response", length = 1000)
    private String adminResponse;

    // ----- Behaviour -----

    /** HIGH / MEDIUM / LOW - worked out by the right responder for this feedback type. */
    public String getPriority() {
        return FeedbackResponder.forType(feedbackType).calculatePriority(this);
    }

    /** A ready-made reply suggestion for the admin. */
    public String getSuggestedResponse() {
        return FeedbackResponder.forType(feedbackType).suggestResponse(this);
    }

    /** Stars for showing the rating, for example 4 -> 4 filled stars and 1 empty star. */
    public String getStars() {
        int r = (rating == null) ? 0 : rating;
        // ★ is a filled star and ☆ is an empty star
        return "★".repeat(r) + "☆".repeat(5 - r);
    }

    // ----- Getters and setters -----

    public Long getFeedbackId() {
        return feedbackId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

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

    public FeedbackStatus getStatus() {
        return status;
    }

    public void setStatus(FeedbackStatus status) {
        this.status = status;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }

    @Override
    public String toString() {
        return "Feedback{id=" + feedbackId + ", type=" + feedbackType + ", rating=" + rating + ", status=" + status + "}";
    }
}