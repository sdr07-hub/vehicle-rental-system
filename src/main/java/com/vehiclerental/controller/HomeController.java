package com.vehiclerental.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        // Nobody sees the home page until they have logged in
        if (session.getAttribute("loggedInUserId") == null) {
            return "redirect:/login";
        }
        return "home";
    }
}