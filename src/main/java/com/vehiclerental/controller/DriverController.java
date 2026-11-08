package com.vehiclerental.controller;

import com.vehiclerental.model.Driver;
import com.vehiclerental.model.DriverAvailability;
import com.vehiclerental.service.DriverService;
import com.vehiclerental.util.FormErrors;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
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

import java.util.Optional;

/**
 * Web pages for the Driver module.
 *
 *  GET  /drivers                    -> list + search                (READ)
 *  GET  /drivers/new                -> registration form
 *  POST /drivers                    -> save new driver              (CREATE)
 *  GET  /drivers/{id}/edit          -> edit form
 *  POST /drivers/{id}               -> save changes                 (UPDATE)
 *  POST /drivers/{id}/delete        -> remove driver                (DELETE)
 *  GET  /drivers/{id}/trips         -> mobile-friendly trip page
 *  POST /drivers/{id}/availability  -> quick availability change    (UPDATE)
 */
@Controller
@RequestMapping("/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // ---------- READ ----------
    @GetMapping
    public String list(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        model.addAttribute("drivers", driverService.search(keyword));
        model.addAttribute("keyword", keyword);
        return "driver/list";
    }

    // ---------- CREATE ----------
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("driver", new Driver());
        prepareForm(model, false, null);
        return "driver/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("driver") Driver driver,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (driver.getPassword() == null || driver.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "password.short", "Password must be at least 6 characters");
        }
        checkDuplicates(driver, null, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            prepareForm(model, false, null);
            return "driver/form";
        }

        Driver saved = driverService.register(driver);
        redirectAttributes.addFlashAttribute("successMessage",
                "Driver " + saved.getFullName() + " registered successfully.");
        return "redirect:/drivers";
    }

    // ---------- UPDATE ----------
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Driver> driver = driverService.findById(id);
        if (driver.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Driver not found.");
            return "redirect:/drivers";
        }
        model.addAttribute("driver", driver.get());
        prepareForm(model, true, id);
        return "driver/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("driver") Driver driver,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (driver.getPassword() != null && !driver.getPassword().isBlank()
                && driver.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "password.short", "New password must be at least 6 characters");
        }
        checkDuplicates(driver, id, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            prepareForm(model, true, id);
            return "driver/form";
        }

        driverService.update(id, driver);
        redirectAttributes.addFlashAttribute("successMessage", "Driver details updated.");
        return "redirect:/drivers";
    }

    // Quick availability change from the trip page
    @PostMapping("/{id}/availability")
    public String changeAvailability(@PathVariable("id") Long id,
                                     @RequestParam(name = "availability") DriverAvailability availability,
                                     RedirectAttributes redirectAttributes) {
        driverService.updateAvailability(id, availability);
        redirectAttributes.addFlashAttribute("successMessage", "Availability changed to " + availability.getLabel() + ".");
        return "redirect:/drivers/" + id + "/trips";
    }

    // ---------- DRIVER TRIP PAGE (mobile-friendly) ----------
    @GetMapping("/{id}/trips")
    public String trips(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Driver> driver = driverService.findById(id);
        if (driver.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Driver not found.");
            return "redirect:/drivers";
        }
        model.addAttribute("driver", driver.get());
        model.addAttribute("trips", driverService.findTrips(id));
        model.addAttribute("availabilities", DriverAvailability.values());
        return "driver/trips";
    }

    // ---------- DELETE ----------
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            driverService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Driver deleted.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This driver cannot be deleted because other records use it. Deactivate the driver instead.");
        }
        return "redirect:/drivers";
    }

    // ---------- helper methods (private = only this class can use them) ----------

    private void prepareForm(Model model, boolean editMode, Long driverId) {
        model.addAttribute("editMode", editMode);
        model.addAttribute("driverId", driverId);
        model.addAttribute("availabilities", DriverAvailability.values());
    }

    private void checkDuplicates(Driver driver, Long id, BindingResult bindingResult) {
        if (driver.getEmail() != null && driverService.isEmailTaken(driver.getEmail(), id)) {
            bindingResult.rejectValue("email", "email.taken", "This email is already used by another user");
        }
        if (driver.getLicenceNumber() != null && !driver.getLicenceNumber().isBlank()
                && driverService.isLicenceTaken(driver.getLicenceNumber().trim(), id)) {
            bindingResult.rejectValue("licenceNumber", "licence.taken", "This licence number is already registered");
        }
    }
}
