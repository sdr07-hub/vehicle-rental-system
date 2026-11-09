package com.vehiclerental.controller;

import com.vehiclerental.model.Customer;
import com.vehiclerental.model.User;
import com.vehiclerental.service.AuthService;
import com.vehiclerental.service.CustomerService;
import com.vehiclerental.util.FormErrors;
import com.vehiclerental.util.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;

/**
 * Log in, sign up and log out.
 *
 *  GET  /login    -> login page (one page for customers, drivers and admins)
 *  POST /login    -> check email + password, then go to the right dashboard
 *  GET  /signup   -> sign-up page for new customers
 *  POST /signup   -> create the customer account and log them in
 *  /logout        -> end the session
 */
@Controller
public class AuthController {

    private final AuthService authService;
    private final CustomerService customerService; // Member 1's service is reused for registration

    public AuthController(AuthService authService, CustomerService customerService) {
        this.authService = authService;
        this.customerService = customerService;
    }

    // ---------- LOG IN ----------
    @GetMapping("/login")
    public String showLogin(HttpSession session) {
        SessionUser current = SessionUser.from(session);
        if (current != null) {
            return "redirect:" + current.getHomeUrl(); // already logged in
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpServletRequest request,
                        Model model) {

        Optional<User> user = authService.login(email, password);
        if (user.isEmpty()) {
            model.addAttribute("errorMessage", "Wrong email or password, or the account is not active.");
            model.addAttribute("email", email);
            return "auth/login";
        }

        SessionUser sessionUser = startSession(request, user.get());
        return "redirect:" + sessionUser.getHomeUrl();
    }

    // ---------- SIGN UP (new customers) ----------
    @GetMapping("/signup")
    public String showSignup(Model model) {
        model.addAttribute("customer", new Customer());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("customer") Customer customer,
                         BindingResult bindingResult,
                         @RequestParam(name = "confirmPassword", required = false) String confirmPassword,
                         HttpServletRequest request,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (customer.getPassword() == null || customer.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "password.short", "Password must be at least 6 characters");
        }
        if (customer.getEmail() != null && customerService.isEmailTaken(customer.getEmail(), null)) {
            bindingResult.rejectValue("email", "email.taken", "This email is already registered. Please log in.");
        }

        Map<String, String> errors = FormErrors.toMap(bindingResult);
        if (customer.getPassword() != null && !customer.getPassword().equals(confirmPassword)) {
            errors.put("confirmPassword", "The two passwords do not match");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "auth/signup";
        }

        Customer saved = customerService.register(customer); // hashes the password with BCrypt
        startSession(request, saved);                          // log the new customer in straight away
        redirectAttributes.addFlashAttribute("successMessage",
                "Welcome, " + saved.getFullName() + "! Your account is ready. Choose a vehicle to book your first ride.");
        return "redirect:/customer/dashboard";
    }

    // ---------- LOG OUT ----------
    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // forget who was logged in
        }
        redirectAttributes.addFlashAttribute("successMessage", "You have logged out.");
        return "redirect:/login";
    }

    // ---------- helper ----------

    /** Starts a fresh session for this user (a new session id each login is safer). */
    private SessionUser startSession(HttpServletRequest request, User user) {
        HttpSession old = request.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        HttpSession session = request.getSession(true);
        SessionUser sessionUser = new SessionUser(user);
        session.setAttribute(SessionUser.SESSION_KEY, sessionUser);
        return sessionUser;
    }
}