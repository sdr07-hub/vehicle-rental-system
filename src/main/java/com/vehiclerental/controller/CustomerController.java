package com.vehiclerental.controller;

import com.vehiclerental.model.Customer;
import com.vehiclerental.service.CustomerService;
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
 * Web pages for the Customer module.
 *
 *  GET  /customers               -> list + search          (READ)
 *  GET  /customers/new           -> empty registration form
 *  POST /customers               -> save new customer       (CREATE)
 *  GET  /customers/{id}/edit     -> form filled with data
 *  POST /customers/{id}          -> save changes            (UPDATE)
 *  POST /customers/{id}/delete   -> remove customer         (DELETE)
 */
@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // ---------- READ ----------
    @GetMapping
    public String list(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        model.addAttribute("customers", customerService.search(keyword));
        model.addAttribute("keyword", keyword);
        return "customer/list";
    }

    // ---------- CREATE ----------
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("editMode", false);
        return "customer/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("customer") Customer customer,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        // Extra checks that annotations cannot do on their own
        if (customer.getPassword() == null || customer.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "password.short", "Password must be at least 6 characters");
        }
        if (customer.getEmail() != null && customerService.isEmailTaken(customer.getEmail(), null)) {
            bindingResult.rejectValue("email", "email.taken", "This email is already registered");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            model.addAttribute("editMode", false);
            return "customer/form";
        }

        Customer saved = customerService.register(customer);
        redirectAttributes.addFlashAttribute("successMessage",
                "Customer " + saved.getFullName() + " registered successfully.");
        return "redirect:/customers";
    }

    // ---------- UPDATE ----------
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Customer not found.");
            return "redirect:/customers";
        }
        model.addAttribute("customer", customer.get());
        model.addAttribute("customerId", id);
        model.addAttribute("editMode", true);
        return "customer/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("customer") Customer customer,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (customer.getPassword() != null && !customer.getPassword().isBlank()
                && customer.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "password.short", "New password must be at least 6 characters");
        }
        if (customer.getEmail() != null && customerService.isEmailTaken(customer.getEmail(), id)) {
            bindingResult.rejectValue("email", "email.taken", "This email is already used by another user");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            model.addAttribute("customerId", id);
            model.addAttribute("editMode", true);
            return "customer/form";
        }

        customerService.update(id, customer);
        redirectAttributes.addFlashAttribute("successMessage", "Customer details updated.");
        return "redirect:/customers";
    }

    // ---------- DELETE ----------
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Customer deleted.");
        } catch (DataIntegrityViolationException e) {
            // The database refuses because this customer still has bookings
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This customer has bookings, so they cannot be deleted. Edit the customer and untick 'Active' instead.");
        }
        return "redirect:/customers";
    }
}
