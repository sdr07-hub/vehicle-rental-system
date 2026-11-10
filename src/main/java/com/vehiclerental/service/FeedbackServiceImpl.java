
package com.vehiclerental.service;

import com.vehiclerental.model.Feedback;
import com.vehiclerental.model.FeedbackOptionView;
import com.vehiclerental.model.FeedbackStatus;
import com.vehiclerental.model.FeedbackType;
import com.vehiclerental.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Business rules and database calls for feedback and complaints.
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public List<Feedback> search(FeedbackType type, FeedbackStatus status) {
        if (type != null && status != null) {
            return feedbackRepository.findByFeedbackTypeAndStatusOrderByCreatedAtDesc(type, status);
        }
        if (type != null) {
            return feedbackRepository.findByFeedbackTypeOrderByCreatedAtDesc(type);
        }
        if (status != null) {
            return feedbackRepository.findByStatusOrderByCreatedAtDesc(status);
        }
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Optional<Feedback> findById(Long id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public List<FeedbackOptionView> getCustomerOptions() {
        return feedbackRepository.findCustomerOptions();
    }

    @Override
    public List<FeedbackOptionView> getBookingOptions() {
        return feedbackRepository.findBookingOptions();
    }

    @Override
    public Map<Long, String> getCustomerNames() {
        Map<Long, String> names = new HashMap<>();
        for (FeedbackOptionView option : feedbackRepository.findCustomerOptions()) {
            names.put(option.getId(), option.getLabel());
        }
        return names;
    }

    @Override
    public boolean bookingBelongsToCustomer(Long bookingId, Long customerId) {
        return feedbackRepository.countBookingForCustomer(bookingId, customerId) > 0;
    }

    @Override
    @Transactional
    public Feedback submit(Feedback feedback) {
        // New feedback always starts as OPEN with no reply yet
        feedback.setStatus(FeedbackStatus.OPEN);
        feedback.setAdminResponse(null);
        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public Feedback update(Long id, Feedback formData) {
        Feedback existing = feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found: " + id));
        existing.setCustomerId(formData.getCustomerId());
        existing.setBookingId(formData.getBookingId());
        existing.setFeedbackType(formData.getFeedbackType());
        existing.setRating(formData.getRating());
        existing.setSubject(formData.getSubject());
        existing.setMessage(formData.getMessage());
        existing.setStatus(formData.getStatus());
        existing.setAdminResponse(formData.getAdminResponse());
        return feedbackRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        feedbackRepository.deleteById(id);
    }
}
