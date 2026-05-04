package com.example.spring.bpmresolver.controllers.view;

import com.example.spring.bpmresolver.services.UserRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterViewController {

    private final UserRegistrationService userRegistrationService;

    public RegisterViewController(
            UserRegistrationService userRegistrationService
    ) {
        this.userRegistrationService = userRegistrationService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("passwordConfirm") String passwordConfirm,
            Model model
    ) {
        try {
            userRegistrationService.register(username, password, passwordConfirm);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }

        return "redirect:/login";
    }
}
