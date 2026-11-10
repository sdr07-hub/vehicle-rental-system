
package com.vehiclerental.service;

import com.vehiclerental.model.Feedback;
import com.vehiclerental.model.FeedbackOptionView;
import com.vehiclerental.model.FeedbackStatus;
import com.vehiclerental.model.FeedbackType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * What the Feedback module can do.
 * OOP: Abstraction - FeedbackServiceImpl contains the details.
 */
public interface FeedbackService {

    List<Feedback> search(FeedbackType type, FeedbackStatus status);

    Optional<Feedback> findById(Long id);

    List<FeedbackOptionView> getCustomerOptions();

    List<FeedbackOptionView> getBookingOptions();

    /** customer id -> customer name, used to show names on the list page. */
    Map<Long, String> getCustomerNames();

    boolean bookingBelongsToCustomer(Long bookingId, Long customerId);

    Feedback submit(Feedback feedback);

    Feedback update(Long id, Feedback formData);

    void delete(Long id);
}
