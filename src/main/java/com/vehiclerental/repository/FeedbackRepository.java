
package com.vehiclerental.repository;

import com.vehiclerental.model.Feedback;
import com.vehiclerental.model.FeedbackOptionView;
import com.vehiclerental.model.FeedbackStatus;
import com.vehiclerental.model.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Database access for feedback and complaints.
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Spring writes these queries from the method names (newest first)
    List<Feedback> findAllByOrderByCreatedAtDesc();

    List<Feedback> findByFeedbackTypeOrderByCreatedAtDesc(FeedbackType feedbackType);

    List<Feedback> findByStatusOrderByCreatedAtDesc(FeedbackStatus status);

    List<Feedback> findByFeedbackTypeAndStatusOrderByCreatedAtDesc(FeedbackType feedbackType, FeedbackStatus status);

    // Drop-down: all customers (id + name)
    @Query(value = """
       SELECT c.customer_id AS id,
            u.full_name AS label
       FROM customers c
       JOIN users u ON u.user_id = c.customer_id
       ORDER BY u.full_name
       """, nativeQuery = true)
    List<FeedbackOptionView> findCustomerOptions();

    // Drop-down: bookings, newest first
    @Query(value = """
       SELECT b.booking_id AS id,
            CONCAT('#', b.booking_id, ' - ', u.full_name, ': ',
                b.pickup_location, ' to ', b.drop_location) AS label
       FROM bookings b
       JOIN users u ON u.user_id = b.customer_id
       ORDER BY b.booking_id DESC
       """, nativeQuery = true)
    List<FeedbackOptionView> findBookingOptions();

    // 1 if the booking belongs to this customer, otherwise 0
    @Query(value = "SELECT COUNT(*) FROM bookings WHERE booking_id = :bookingId AND customer_id = :customerId",
            nativeQuery = true)
    long countBookingForCustomer(@Param("bookingId") Long bookingId, @Param("customerId") Long customerId);
}
