package com.vehiclerental.portal.controller;

import com.vehiclerental.model.Feedback;
import com.vehiclerental.model.FeedbackType;
import com.vehiclerental.portal.model.FeedbackForm;
import com.vehiclerental.service.FeedbackService;
import com.vehiclerental.util.FormErrors;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Feedback pages for a logged-in customer, in the new interface.
 * FeedbackWebConfig protects the customer feedback routes for customers only.
 * Saving and the priority rules are done by Member 6's FeedbackService (reuse, not a copy).
 *
 *  GET  /customer/feedback               -> my feedback and the admin's replies
 *  GET  /customer/feedback/new?rideId=5  -> feedback form (optionally about one ride)
 *  POST /customer/feedback               -> save it
 */
@Controller
@RequestMapping("/customer/feedback")
public class CustomerFeedbackController {

    private final FeedbackService feedbackService;

    public CustomerFeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // ---------- MY FEEDBACK ----------
    @GetMapping
    public String myFeedback(HttpSession session, Model model) {
        Long customerId = currentUserId(session);
        // search(null, null) = all feedback, newest first; keep only this customer's items
        List<Feedback> mine = feedbackService.search(null, null).stream()
                .filter(f -> customerId.equals(f.getCustomerId()))
                .toList();
        model.addAttribute("feedbackList", mine);
        return "portal/my-feedback";
    }

    // ---------- FORM ----------
    @GetMapping("/new")
    public String showForm(@RequestParam(name = "rideId", required = false) Long rideId,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        FeedbackForm form = new FeedbackForm();
        if (rideId != null) {
            if (!feedbackService.bookingBelongsToCustomer(rideId, currentUserId(session))) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ride not found.");
                return "redirect:/customer/feedback";
            }
            form.setBookingId(rideId);
            form.setSubject("Ride #" + rideId);
        }
        model.addAttribute("feedbackForm", form);
        model.addAttribute("types", FeedbackType.values());
        return "portal/feedback-form";
    }

    // ---------- SAVE ----------
    @PostMapping
    public String submit(@Valid @ModelAttribute("feedbackForm") FeedbackForm form,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        Long customerId = currentUserId(session);

        // A customer may only give feedback about their own ride
        if (form.getBookingId() != null && !feedbackService.bookingBelongsToCustomer(form.getBookingId(), customerId)) {
            bindingResult.rejectValue("bookingId", "booking.notYours", "You can only give feedback on your own rides");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            model.addAttribute("types", FeedbackType.values());
            return "portal/feedback-form";
        }

        // Turn the form into Member 6's Feedback entity. The customer id comes from the session, never the form.
        Feedback feedback = new Feedback();
        feedback.setCustomerId(customerId);
        feedback.setBookingId(form.getBookingId());
        feedback.setFeedbackType(form.getFeedbackType());
        feedback.setRating(form.getRating());
        feedback.setSubject(form.getSubject().trim());
        feedback.setMessage(form.getMessage().trim());

        Feedback saved = feedbackService.submit(feedback); // status OPEN, no reply yet
        redirectAttributes.addFlashAttribute("successMessage",
                "Thank you! Your " + saved.getFeedbackType().getLabel().toLowerCase()
                        + " was sent. The admin's reply will appear on this page.");
        return "redirect:/customer/feedback";
    }

    private Long currentUserId(HttpSession session) {
        return (Long) session.getAttribute("loggedInUserId"); // checked by FeedbackWebConfig
    }
}
