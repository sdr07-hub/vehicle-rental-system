package com.vehiclerental.controller;

import com.vehiclerental.model.FareCalculator;
import com.vehiclerental.model.FareRule;
import com.vehiclerental.service.FareRuleService;
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

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Fare rule management (part of the Admin and Fare module).
 *
 *  GET  /admin/fares                -> list fare rules        (READ)
 *  GET  /admin/fares/new            -> add form
 *  POST /admin/fares                -> save new rule          (CREATE)
 *  GET  /admin/fares/{id}/edit      -> edit form
 *  POST /admin/fares/{id}           -> save changes           (UPDATE)
 *  POST /admin/fares/{id}/delete    -> remove rule            (DELETE)
 *  GET  /admin/fares/estimate       -> fare calculator page
 */
@Controller
@RequestMapping("/admin/fares")
public class FareRuleController {

    private static final String[] VEHICLE_TYPES = {"CAR", "VAN", "SUV"};

    private final FareRuleService fareRuleService;

    public FareRuleController(FareRuleService fareRuleService) {
        this.fareRuleService = fareRuleService;
    }

    // ---------- READ ----------
    @GetMapping
    public String list(Model model) {
        model.addAttribute("fareRules", fareRuleService.findAll());
        return "admin/fare-list";
    }

    // ---------- CREATE ----------
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("fareRule", new FareRule());
        prepareForm(model, false, null);
        return "admin/fare-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("fareRule") FareRule fareRule,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        checkRules(fareRule, null, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            prepareForm(model, false, null);
            return "admin/fare-form";
        }

        fareRuleService.create(fareRule);
        redirectAttributes.addFlashAttribute("successMessage", "Fare rule for " + fareRule.getVehicleType() + " added.");
        return "redirect:/admin/fares";
    }

    // ---------- UPDATE ----------
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<FareRule> fareRule = fareRuleService.findById(id);
        if (fareRule.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Fare rule not found.");
            return "redirect:/admin/fares";
        }
        model.addAttribute("fareRule", fareRule.get());
        prepareForm(model, true, id);
        return "admin/fare-form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("fareRule") FareRule fareRule,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        checkRules(fareRule, id, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            prepareForm(model, true, id);
            return "admin/fare-form";
        }

        fareRuleService.update(id, fareRule);
        redirectAttributes.addFlashAttribute("successMessage", "Fare rule updated.");
        return "redirect:/admin/fares";
    }

    // ---------- DELETE ----------
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        fareRuleService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Fare rule deleted.");
        return "redirect:/admin/fares";
    }

    // ---------- FARE CALCULATOR ----------
    @GetMapping("/estimate")
    public String estimate(@RequestParam(name = "vehicleType", required = false) String vehicleType,
                           @RequestParam(name = "distanceKm", required = false) BigDecimal distanceKm,
                           @RequestParam(name = "night", defaultValue = "false") boolean night,
                           Model model) {
        model.addAttribute("vehicleTypes", VEHICLE_TYPES);
        model.addAttribute("vehicleType", vehicleType);
        model.addAttribute("distanceKm", distanceKm);
        model.addAttribute("night", night);

        if (vehicleType != null && distanceKm != null) {
            if (distanceKm.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("errorMessage", "Distance must be more than 0 km.");
            } else {
                Optional<FareCalculator> calculator = fareRuleService.getCalculator(vehicleType, night);
                if (calculator.isEmpty()) {
                    model.addAttribute("errorMessage", "There is no active fare rule for " + vehicleType + ".");
                } else {
                    // POLYMORPHISM: this line works the same for FareRule and NightFareCalculator
                    model.addAttribute("fare", calculator.get().calculateFare(distanceKm));
                    model.addAttribute("calculatorName", calculator.get().getCalculatorName());
                }
            }
        }
        return "admin/fare-estimate";
    }

    // ---------- helper methods ----------

    private void prepareForm(Model model, boolean editMode, Long fareRuleId) {
        model.addAttribute("editMode", editMode);
        model.addAttribute("fareRuleId", fareRuleId);
        model.addAttribute("vehicleTypes", VEHICLE_TYPES);
    }

    private void checkRules(FareRule fareRule, Long id, BindingResult bindingResult) {
        if (fareRule.getVehicleType() != null && !fareRule.getVehicleType().isBlank()
                && fareRuleService.isVehicleTypeTaken(fareRule.getVehicleType(), id)) {
            bindingResult.rejectValue("vehicleType", "type.taken",
                    "A fare rule for " + fareRule.getVehicleType() + " already exists. Edit that one instead.");
        }
        if (fareRule.getMinimumFare() != null && fareRule.getBaseFare() != null
                && fareRule.getMinimumFare().compareTo(fareRule.getBaseFare()) < 0) {
            bindingResult.rejectValue("minimumFare", "min.low", "Minimum fare should not be less than the base fare");
        }
    }
}
