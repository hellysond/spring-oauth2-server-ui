package com.hellysond.spring.oauth2.server.ui.controller;

import com.hellysond.spring.oauth2.server.ui.api.repository.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("user", userService.getAllUsers());

        return "dashboard";
    }
}