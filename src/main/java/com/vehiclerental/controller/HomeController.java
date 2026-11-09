package com.vehiclerental.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Shows the public home page. Logged-in users see a "Go to my dashboard" button on it.
 * (Logging in and out is handled by auth/AuthController.)
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }
}
