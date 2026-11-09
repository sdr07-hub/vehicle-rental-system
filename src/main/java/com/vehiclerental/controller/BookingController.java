package com.vehiclerental.controller;

import com.vehiclerental.model.Booking;
import com.vehiclerental.model.BookingStatus;
import com.vehiclerental.service.BookingService;
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

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Web pages for the Booking module (admin only - see WebConfig).
 *
 *  GET  /bookings               -> list with status filter + search  (READ)
 *  GET  /bookings/new           -> booking form
 *  POST /bookings               -> save new booking                   (CREATE)
 *  GET  /bookings/{id}/edit     -> edit form (assign driver, change status)
 *  POST /bookings/{id}          -> save changes                       (UPDATE)
 *  POST /bookings/{id}/cancel   -> cancel booking                     (UPDATE)
 *  POST /bookings/{id}/delete   -> remove booking                     (DELETE)
 */
@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ---------- READ ----------
    @GetMapping
    public String list(@RequestParam(name = "status", required = false) String status,
                       @RequestParam(name = "keyword", required = false) String keyword,
                       Model model) {
        model.addAttribute("bookings", bookingService.search(status, keyword));
        model.addAttribute("statuses", BookingStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "booking/list";
    }

    // ---------- CREATE ----------
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Booking booking = new Booking();
        booking.setPickupTime(LocalDateTime.now().plusHours(1).withSecond(0).withNano(0));
        model.addAttribute("booking", booking);
        prepareForm(model, false, null);
        return "booking/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("booking") Booking booking,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (booking.getPickupTime() != null && booking.getPickupTime().isBefore(LocalDateTime.now())) {
            bindingResult.rejectValue("pickupTime", "time.past", "Pickup time must be in the future");
        }
        checkFareRuleExists(booking, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            prepareForm(model, false, null);
            return "booking/form";
        }

        Booking saved = bookingService.create(booking);
        redirectAttributes.addFlashAttribute("successMessage",
                "Booking #" + saved.getBookingId() + " created. Estimated fare: Rs. " + saved.getEstimatedFare()
                        + " (" + bookingService.choosePricing(saved.getPickupTime()).getName() + " pricing).");
        return "redirect:/bookings";
    }

    // ---------- UPDATE ----------
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Booking> booking = bookingService.findById(id);
        if (booking.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found.");
            return "redirect:/bookings";
        }
        if (booking.get().getStatus().isFinished()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Booking #" + id + " is " + booking.get().getStatus().getLabel() + " and can no longer be changed.");
            return "redirect:/bookings";
        }
        model.addAttribute("booking", booking.get());
        prepareForm(model, true, id);
        return "booking/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("booking") Booking booking,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        Optional<Booking> existing = bookingService.findById(id);
        if (existing.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking not found.");
            return "redirect:/bookings";
        }

        BookingStatus currentStatus = existing.get().getStatus();
        BookingStatus newStatus = booking.getStatus();
        if (newStatus == null) {
            bindingResult.rejectValue("status", "status.missing", "Please choose a status");
        } else {
            if (!currentStatus.canMoveTo(newStatus)) {
                bindingResult.rejectValue("status", "status.invalid",
                        "Status cannot change from " + currentStatus.getLabel() + " to " + newStatus.getLabel());
            }
            boolean needsDriver = newStatus == BookingStatus.CONFIRMED
                    || newStatus == BookingStatus.IN_PROGRESS
                    || newStatus == BookingStatus.COMPLETED;
            if (needsDriver && booking.getDriverId() == null) {
                bindingResult.rejectValue("driverId", "driver.missing", "Assign a driver before choosing this status");
            }
        }
        checkFareRuleExists(booking, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            prepareForm(model, true, id);
            return "booking/form";
        }

        bookingService.update(id, booking);
        redirectAttributes.addFlashAttribute("successMessage", "Booking #" + id + " updated.");
        return "redirect:/bookings";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancel(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking #" + id + " cancelled.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/bookings";
    }

    // ---------- DELETE ----------
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        bookingService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Booking #" + id + " deleted.");
        return "redirect:/bookings";
    }

    // ---------- helper methods ----------

    private void prepareForm(Model model, boolean editMode, Long bookingId) {
        model.addAttribute("editMode", editMode);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("customerOptions", bookingService.getCustomerOptions());
        model.addAttribute("vehicleOptions", bookingService.getVehicleOptions());
        model.addAttribute("driverOptions", bookingService.getDriverOptions());
        model.addAttribute("statuses", BookingStatus.values());
    }

    private void checkFareRuleExists(Booking booking, BindingResult bindingResult) {
        if (booking.getVehicleId() != null && booking.getDistanceKm() != null
                && bookingService.estimateFare(booking.getVehicleId(), booking.getDistanceKm(), booking.getPickupTime()) == null) {
            bindingResult.rejectValue("vehicleId", "fare.missing",
                    "There is no active fare rule for this vehicle type. Ask the admin to add one.");
        }
    }
}
