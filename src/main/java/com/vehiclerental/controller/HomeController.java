package com.vehiclerental.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles the main landing page of the application.
 */
@Controller
public class HomeController {

    // Runs when someone opens http://localhost:8081/
    @GetMapping("/")
    public String home() {
        // "home" means: show /WEB-INF/views/home.jsp
        return "home";
    }
}