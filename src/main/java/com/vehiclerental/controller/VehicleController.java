package com.vehiclerental.controller;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.model.VehicleFactory;
import com.vehiclerental.model.VehicleForm;
import com.vehiclerental.model.VehicleStatus;
import com.vehiclerental.service.VehicleService;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final SpringValidatorAdapter formValidator;

    public VehicleController(VehicleService vehicleService, Validator validator) {
        this.vehicleService = vehicleService;
        this.formValidator = new SpringValidatorAdapter(validator);
    }

    @InitBinder("vehicleForm")
    public void configureBinding(WebDataBinder binder) {
        binder.setAllowedFields("vehicleType", "registrationNumber", "brand", "model",
                "manufactureYear", "seatingCapacity", "colour", "status");
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("vehicles", vehicleService.search(keyword));
        model.addAttribute("keyword", keyword);
        return "vehicle/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicleForm", new VehicleForm());
        prepareForm(model, false, null);
        return "vehicle/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("vehicleForm") VehicleForm form, BindingResult bindingResult,
                         Model model, RedirectAttributes redirectAttributes) {
        checkBusinessRules(form, null, bindingResult);
        if (bindingResult.hasErrors()) {
            return showInvalidForm(model, bindingResult, false, null);
        }
        try {
            Vehicle vehicle = vehicleService.create(form);
            redirectAttributes.addFlashAttribute("successMessage",
                    vehicle.getTypeName() + " " + vehicle.getRegistrationNumber() + " added successfully.");
            return "redirect:/vehicles";
        } catch (DataIntegrityViolationException exception) {
            rejectSaveFailure(form, null, bindingResult);
            return showInvalidForm(model, bindingResult, false, null);
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Vehicle> vehicle = vehicleService.findById(id);
        if (vehicle.isEmpty()) {
            return vehicleNotFound(redirectAttributes);
        }
        model.addAttribute("vehicleForm", VehicleForm.fromVehicle(vehicle.get()));
        prepareForm(model, true, id);
        return "vehicle/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("vehicleForm") VehicleForm form,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        Optional<Vehicle> existing = vehicleService.findById(id);
        if (existing.isEmpty()) {
            return vehicleNotFound(redirectAttributes);
        }
        // Restore the trusted subtype BEFORE validation, even if the hidden input was forged or omitted.
        form.setVehicleType(existing.get().getTypeCode());
        formValidator.validate(form, bindingResult);
        checkBusinessRules(form, id, bindingResult);
        if (bindingResult.hasErrors()) {
            return showInvalidForm(model, bindingResult, true, id);
        }
        try {
            vehicleService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle details updated.");
            return "redirect:/vehicles";
        } catch (DataIntegrityViolationException exception) {
            rejectSaveFailure(form, id, bindingResult);
            return showInvalidForm(model, bindingResult, true, id);
        } catch (IllegalArgumentException exception) {
            // The record may have been removed between loading the form and saving it.
            return vehicleNotFound(redirectAttributes);
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (vehicleService.findById(id).isEmpty()) {
            return vehicleNotFound(redirectAttributes);
        }
        try {
            vehicleService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle removed.");
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This vehicle is used in bookings, so it cannot be removed. Set its status to 'Under maintenance' instead.");
        } catch (IllegalStateException | InvalidDataAccessResourceUsageException exception) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Vehicle removal is unavailable because the booking database relationship could not be verified. Please check the database setup.");
        } catch (IllegalArgumentException exception) {
            return vehicleNotFound(redirectAttributes);
        }
        return "redirect:/vehicles";
    }

    private void prepareForm(Model model, boolean editMode, Long vehicleId) {
        model.addAttribute("editMode", editMode);
        model.addAttribute("vehicleId", vehicleId);
        model.addAttribute("vehicleTypes", VehicleFactory.TYPE_CODES.clone());
        model.addAttribute("statuses", VehicleStatus.values());
        model.addAttribute("currentYear", Year.now().getValue());
    }

    private void checkBusinessRules(VehicleForm form, Long id, BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors("vehicleType")) {
            if (!VehicleFactory.isValidType(form.getVehicleType())) {
                bindingResult.rejectValue("vehicleType", "vehicleType.invalid", "Please choose a valid vehicle type");
            } else if (!bindingResult.hasFieldErrors("seatingCapacity") && form.getSeatingCapacity() != null) {
                Vehicle sample = VehicleFactory.create(form.getVehicleType());
                if (form.getSeatingCapacity() > sample.getMaxSeats()) {
                    bindingResult.rejectValue("seatingCapacity", "seatingCapacity.limit",
                            "A " + sample.getTypeName() + " can have at most " + sample.getMaxSeats() + " seats");
                }
            }
        }
        if (!bindingResult.hasFieldErrors("manufactureYear") && form.getManufactureYear() != null
                && form.getManufactureYear() > Year.now().getValue()) {
            bindingResult.rejectValue("manufactureYear", "manufactureYear.future", "Manufacture year must not be in the future");
        }
        if (!bindingResult.hasFieldErrors("registrationNumber")
                && vehicleService.isRegistrationTaken(form.getRegistrationNumber(), id)) {
            bindingResult.rejectValue("registrationNumber", "registrationNumber.duplicate", "This registration number already exists");
        }
    }

    private void rejectSaveFailure(VehicleForm form, Long id, BindingResult bindingResult) {
        // Also handle two simultaneous submissions that passed the initial uniqueness check.
        if (vehicleService.isRegistrationTaken(form.getRegistrationNumber(), id)) {
            bindingResult.rejectValue("registrationNumber", "registrationNumber.duplicate", "This registration number already exists");
        } else {
            bindingResult.reject("vehicle.save", "Vehicle could not be saved. Please try again.");
        }
    }

    private String showInvalidForm(Model model, BindingResult bindingResult, boolean editMode, Long vehicleId) {
        // Build field messages locally; no shared FormErrors utility is required.
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            String message = error.isBindingFailure() ? switch (error.getField()) {
                case "manufactureYear", "seatingCapacity" -> "Please enter a whole number";
                case "status" -> "Please choose a valid status";
                default -> "Please enter a valid value";
            } : error.getDefaultMessage();
            errors.putIfAbsent(error.getField(), message);
        }
        model.addAttribute("errors", errors);
        model.addAttribute("globalErrors", bindingResult.getGlobalErrors());
        prepareForm(model, editMode, vehicleId);
        return "vehicle/form";
    }

    private String vehicleNotFound(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Vehicle not found.");
        return "redirect:/vehicles";
    }
}
