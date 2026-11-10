
package com.vehiclerental.controller;

import com.vehiclerental.model.Feedback;
import com.vehiclerental.model.FeedbackStatus;
import com.vehiclerental.model.FeedbackType;
import com.vehiclerental.service.FeedbackService;
import com.vehiclerental.util.FormErrors;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Web pages for the Feedback and Complaint module.
 *
 * GET /feedback         -> list with type/status filters (READ)
 * GET /feedback/new         -> feedback form
 * POST /feedback         -> save new feedback             (CREATE)
 * GET /feedback/{id}/edit -> review form (status + reply)
 * POST /feedback/{id}      -> save changes              (UPDATE)
 * POST /feedback/{id}/delete -> remove feedback              (DELETE)
 */
@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // ---------- READ ----------
    @GetMapping
    public String list(@RequestParam(name = "type", required = false) FeedbackType type,
                       @RequestParam(name = "status", required = false) FeedbackStatus status,
                       @RequestParam(name = "keyword", required = false) String keyword,
                       Model model) {
        Map<Long, String> customerNames = feedbackService.getCustomerNames();
        List<Feedback> results = feedbackService.search(type, status);

        // Keyword search: keep feedback whose subject, message or customer name contains the words typed
        String k = (keyword == null) ? "" : keyword.trim().toLowerCase();
        if (!k.isEmpty()) {
            results = results.stream()
                    .filter(f -> contains(f.getSubject(), k)
                            || contains(f.getMessage(), k)
                            || contains(customerNames.get(f.getCustomerId()), k))
                    .toList();
        }

        model.addAttribute("feedbackList", results);
        model.addAttribute("customerNames", customerNames);
        model.addAttribute("keyword", keyword);
        model.addAttribute("types", FeedbackType.values());
        model.addAttribute("statuses", FeedbackStatus.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);
        return "feedback/list";
    }

    // ---------- CREATE ----------
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Feedback feedback = new Feedback();
        feedback.setRating(5);
        model.addAttribute("feedback", feedback);
        prepareForm(model, false, null);
        return "feedback/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("feedback") Feedback feedback,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        checkBooking(feedback, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            prepareForm(model, false, null);
            return "feedback/form";
        }

        Feedback saved = feedbackService.submit(feedback);
        redirectAttributes.addFlashAttribute("successMessage",
                saved.getFeedbackType().getLabel() + " submitted. Priority: " + saved.getPriority() + ".");
        return "redirect:/feedback";
    }

    // ---------- UPDATE ----------
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        Optional<Feedback> feedback = feedbackService.findById(id);
        if (feedback.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Feedback not found.");
            return "redirect:/feedback";
        }
        Feedback f = feedback.get();
        // Fill in a suggested reply if the admin has not written one yet
        if (f.getAdminResponse() == null || f.getAdminResponse().isBlank()) {
            f.setAdminResponse(f.getSuggestedResponse());
        }
        model.addAttribute("feedback", f);
        prepareForm(model, true, id);
        return "feedback/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("feedback") Feedback feedback,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        checkBooking(feedback, bindingResult);

        if (feedback.getStatus() == null) {
            bindingResult.rejectValue("status", "status.missing", "Please choose a status");
        } else if (feedback.getStatus().isFinished()
                && feedback.getFeedbackType() == FeedbackType.COMPLAINT
                && (feedback.getAdminResponse() == null || feedback.getAdminResponse().isBlank())) {
            // Rule: a complaint cannot be resolved or closed without a reply to the customer
            bindingResult.rejectValue("adminResponse", "response.missing",
                    "Write a reply to the customer before resolving or closing a complaint");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            prepareForm(model, true, id);
            return "feedback/form";
        }

        feedbackService.update(id, feedback);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback #" + id + " updated.");
        return "redirect:/feedback";
    }

    // ---------- DELETE ----------
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        feedbackService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback deleted.");
        return "redirect:/feedback";
    }

    // ---------- helper methods ----------

    private void prepareForm(Model model, boolean editMode, Long feedbackId) {
        model.addAttribute("editMode", editMode);
        model.addAttribute("feedbackId", feedbackId);
        model.addAttribute("customerOptions", feedbackService.getCustomerOptions());
        model.addAttribute("bookingOptions", feedbackService.getBookingOptions());
        model.addAttribute("types", FeedbackType.values());
        model.addAttribute("statuses", FeedbackStatus.values());
    }

    private void checkBooking(Feedback feedback, BindingResult bindingResult) {
        if (feedback.getBookingId() != null && feedback.getCustomerId() != null
                && !feedbackService.bookingBelongsToCustomer(feedback.getBookingId(), feedback.getCustomerId())) {
            bindingResult.rejectValue("bookingId", "booking.wrongCustomer",
                    "This booking belongs to a different customer");
        }
    }

    // true if text is not empty and contains the keyword (ignoring upper/lower case)
    private boolean contains(String text, String keyword) {
        return text != null && text.toLowerCase().contains(keyword);
    }
}
