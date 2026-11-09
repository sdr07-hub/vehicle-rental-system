package com.vehiclerental.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // The start page is public: everyone chooses their role here first
    @GetMapping("/")
    public String home() {
        return "home";
    }
}