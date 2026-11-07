package com.vehiclerental.controller;

import com.vehiclerental.model.Admin;
import com.vehiclerental.model.AdminLevel;
import com.vehiclerental.service.AdminService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Admin dashboard and admin accounts.
 *
 *  GET  /admin                      -> dashboard with counts and links
 *  GET  /admin/admins               -> list admins              (READ)
 *  GET  /admin/admins/new           -> add form
 *  POST /admin/admins               -> save new admin           (CREATE)
 *  GET  /admin/admins/{id}/edit     -> edit form
 *  POST /admin/admins/{id}          -> save changes             (UPDATE)
 *  POST /admin/admins/{id}/delete   -> remove admin             (DELETE)
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ---------- DASHBOARD ----------
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("stats", adminService.getDashboardStats());
        return "admin/dashboard";
    }

    // ---------- READ ----------
    @GetMapping("/admins")
    public String list(Model model) {
        model.addAttribute("admins", adminService.findAll());
        return "admin/admin-list";
    }

    // ---------- CREATE ----------
    @GetMapping("/admins/new")
    public String showCreateForm(Model model) {
        model.addAttribute("admin", new Admin());
        prepareForm(model, false, null);
        return "admin/admin-form";
    }

    @PostMapping("/admins")
    public String create(@Valid @ModelAttribute("admin") Admin admin,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (admin.getPassword() == null || admin.getPassword().length() < 8) {
            bindingResult.rejectValue("password", "password.short", "Admin passwords must be at least 8 characters");
        }
        if (admin.getEmail() != null && adminService.isEmailTaken(admin.getEmail(), null)) {
            bindingResult.rejectValue("email", "email.taken", "This email is already registered");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            prepareForm(model, false, null);
            return "admin/admin-form";
        }

        Admin saved = adminService.create(admin);
        redirectAttributes.addFlashAttribute("successMessage", "Admin " + saved.getFullName() + " added.");
        return "redirect:/admin/admins";
    }

    // ---------- UPDATE ----------
    @GetMapping("/admins/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Admin> admin = adminService.findById(id);
        if (admin.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Admin not found.");
            return "redirect:/admin/admins";
        }
        model.addAttribute("admin", admin.get());
        prepareForm(model, true, id);
        return "admin/admin-form";
    }

    @PostMapping("/admins/{id}")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("admin") Admin admin,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (admin.getPassword() != null && !admin.getPassword().isBlank() && admin.getPassword().length() < 8) {
            bindingResult.rejectValue("password", "password.short", "New password must be at least 8 characters");
        }
        if (admin.getEmail() != null && adminService.isEmailTaken(admin.getEmail(), id)) {
            bindingResult.rejectValue("email", "email.taken", "This email is already used by another user");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.toMap(bindingResult));
            prepareForm(model, true, id);
            return "admin/admin-form";
        }

        adminService.update(id, admin);
        redirectAttributes.addFlashAttribute("successMessage", "Admin details updated.");
        return "redirect:/admin/admins";
    }

    // ---------- DELETE ----------
    @PostMapping("/admins/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Admin deleted.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/admins";
    }

    private void prepareForm(Model model, boolean editMode, Long adminId) {
        model.addAttribute("editMode", editMode);
        model.addAttribute("adminId", adminId);
        model.addAttribute("levels", AdminLevel.values());
    }
}
