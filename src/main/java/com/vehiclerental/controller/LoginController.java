package com.vehiclerental.controller;

import com.vehiclerental.model.User;
import com.vehiclerental.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Shows the login page and checks the email and password.
 * After a successful login, the user is sent to the page for their role.
 */
@Controller
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

        Optional<User> found = userRepository.findByEmailIgnoreCase(email.trim());

        // Same message for "no such email" and "wrong password", so attackers learn nothing
        if (found.isEmpty() || !found.get().isActive()
                || !passwordEncoder.matches(password, found.get().getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wrong email or password.");
            return "redirect:/login";
        }

        User user = found.get();
        session.setAttribute("loggedInUserId", user.getUserId());
        session.setAttribute("loggedInName", user.getFullName());
        session.setAttribute("loggedInRole", user.getRole().name());

        redirectAttributes.addFlashAttribute("successMessage", "Welcome, " + user.getFullName() + "!");

        if (user.getRole().name().equals("ADMIN")) {
            return "redirect:/admin";
        }
        if (user.getRole().name().equals("DRIVER")) {
            return "redirect:/drivers";
        }
        return "redirect:/customers";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "You have logged out.");
        return "redirect:/login";
    }
}