package com.vehiclerental.portal.controller;

import com.vehiclerental.auth.SessionUser;
import com.vehiclerental.portal.model.RideStatus;
import com.vehiclerental.portal.model.RideView;
import com.vehiclerental.portal.service.DriverRideService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Pages for a logged-in driver. RoleInterceptor makes sure only drivers reach them.
 *
 *  GET  /driver/dashboard           -> new ride requests + my rides
 *  POST /driver/rides/{id}/accept   -> take a ride request
 *  POST /driver/rides/{id}/start    -> customer picked up
 *  POST /driver/rides/{id}/finish   -> ride finished, total fare shown
 */
@Controller
@RequestMapping("/driver")
public class DriverPortalController {

    private final DriverRideService rideService;

    public DriverPortalController(DriverRideService rideService) {
        this.rideService = rideService;
    }

    @ModelAttribute("statusMap")
    public Map<String, RideStatus> statusMap() {
        return Arrays.stream(RideStatus.values())
                .collect(Collectors.toMap(Enum::name, Function.identity()));
    }

    @GetMapping({"", "/"})
    public String home() {
        return "redirect:/driver/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        List<RideView> myRides = rideService.getMyRides(currentUserId(session));

        List<RideView> finished = myRides.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .toList();
        BigDecimal earnings = finished.stream()
                .map(RideView::getEstimatedFare)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long activeCount = myRides.stream().filter(RideView::isActive).count();

        model.addAttribute("openRequests", rideService.getOpenRequests());
        model.addAttribute("myRides", myRides);
        model.addAttribute("completedCount", finished.size());
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("earnings", earnings);
        return "portal/driver-dashboard";
    }

    @PostMapping("/rides/{id}/accept")
    public String accept(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            rideService.acceptRide(currentUserId(session), id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "You accepted ride #" + id + ". Head to the pickup location.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/driver/dashboard";
    }

    @PostMapping("/rides/{id}/start")
    public String start(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            rideService.startRide(currentUserId(session), id);
            redirectAttributes.addFlashAttribute("successMessage", "Ride #" + id + " started.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/driver/dashboard";
    }

    @PostMapping("/rides/{id}/finish")
    public String finish(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            RideView done = rideService.finishRide(currentUserId(session), id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Ride #" + id + " finished. Total fare: Rs. " + done.getEstimatedFare()
                            + " - please collect it from the customer.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/driver/dashboard";
    }

    private Long currentUserId(HttpSession session) {
        return SessionUser.from(session).getUserId(); // never null here: RoleInterceptor checked it
    }
}
