package com.vehiclerental.portal.controller;

import com.vehiclerental.auth.SessionUser;
import com.vehiclerental.portal.model.RideRequestForm;
import com.vehiclerental.portal.model.RideStatus;
import com.vehiclerental.portal.model.RideView;
import com.vehiclerental.portal.service.CustomerRideService;
import com.vehiclerental.util.FormErrors;
import jakarta.servlet.http.HttpSession;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Pages for a logged-in customer. RoleInterceptor makes sure only customers reach them.
 *
 *  GET  /customer/dashboard          -> welcome, ride stats, vehicle types, recent rides
 *  GET  /customer/book?type=CAR      -> book-a-ride form
 *  POST /customer/book               -> save the ride (PENDING until a driver accepts)
 *  GET  /customer/rides              -> all my rides with totals
 *  POST /customer/rides/{id}/cancel  -> cancel one of my rides
 */
@Controller
@RequestMapping("/customer")
public class CustomerPortalController {

    private final CustomerRideService rideService;

    public CustomerPortalController(CustomerRideService rideService) {
        this.rideService = rideService;
    }

    /** Available on every page of this controller: "PENDING" -> RideStatus.PENDING (label + badge colour). */
    @ModelAttribute("statusMap")
    public Map<String, RideStatus> statusMap() {
        return Arrays.stream(RideStatus.values())
                .collect(Collectors.toMap(Enum::name, Function.identity()));
    }

    @GetMapping({"", "/"})
    public String home() {
        return "redirect:/customer/dashboard";
    }

    // ---------- DASHBOARD ----------
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        List<RideView> rides = rideService.getMyRides(currentUserId(session));

        long activeRides = rides.stream().filter(RideView::isActive).count();
        BigDecimal totalSpent = rides.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .map(RideView::getEstimatedFare)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("vehicleTypes", rideService.getVehicleTypes());
        model.addAttribute("recentRides", rides.stream().limit(3).toList());
        model.addAttribute("rideCount", rides.size());
        model.addAttribute("activeRides", activeRides);
        model.addAttribute("totalSpent", totalSpent);
        return "portal/customer-dashboard";
    }

    // ---------- BOOK A RIDE ----------
    @GetMapping("/book")
    public String showBookForm(@RequestParam(name = "type", required = false) String type, Model model) {
        RideRequestForm form = new RideRequestForm();
        form.setVehicleType(type);
        form.setPickupTime(LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0));
        model.addAttribute("ride", form);
        model.addAttribute("vehicleTypes", rideService.getVehicleTypes());
        return "portal/book-ride";
    }

    @PostMapping("/book")
    public String bookRide(@Valid @ModelAttribute("ride") RideRequestForm ride,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (ride.getPickupTime() != null && ride.getPickupTime().isBefore(LocalDateTime.now().minusMinutes(1))) {
            bindingResult.rejectValue("pickupTime", "time.past", "Pickup time must be in the future");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            model.addAttribute("vehicleTypes", rideService.getVehicleTypes());
            return "portal/book-ride";
        }

        try {
            RideView saved = rideService.bookRide(currentUserId(session), ride);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ride #" + saved.getBookingId() + " booked. Estimated fare: Rs. " + saved.getEstimatedFare()
                            + " (" + rideService.pricingName(ride.getPickupTime()) + " pricing). A driver will accept it soon.");
            return "redirect:/customer/rides";
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("vehicleTypes", rideService.getVehicleTypes());
            return "portal/book-ride";
        }
    }

    // ---------- MY RIDES ----------
    @GetMapping("/rides")
    public String myRides(HttpSession session, Model model) {
        model.addAttribute("rides", rideService.getMyRides(currentUserId(session)));
        return "portal/my-rides";
    }

    @PostMapping("/rides/{id}/cancel")
    public String cancelRide(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            rideService.cancelRide(currentUserId(session), id);
            redirectAttributes.addFlashAttribute("successMessage", "Ride #" + id + " cancelled.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customer/rides";
    }

    // ---------- helper ----------
    private Long currentUserId(HttpSession session) {
        return SessionUser.from(session).getUserId(); // never null here: RoleInterceptor checked it
    }
}
