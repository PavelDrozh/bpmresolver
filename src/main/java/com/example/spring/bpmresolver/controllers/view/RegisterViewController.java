package com.example.spring.bpmresolver.controllers.view;

import com.example.spring.bpmresolver.entities.UserAccount;
import com.example.spring.bpmresolver.entities.UserRole;
import com.example.spring.bpmresolver.repositories.UserAccountRepository;
import com.example.spring.bpmresolver.repositories.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterViewController {

    private final UserAccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterViewController(
            UserAccountRepository userAccountRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userAccountRepository = userAccountRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }

    @PostMapping("/register")
    @Transactional
    public String register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("passwordConfirm") String passwordConfirm,
            Model model
    ) {
        if (username == null || username.isBlank()) {
            model.addAttribute("error", "Логин обязателен");
            return "register";
        }
        if (password == null || password.isBlank()) {
            model.addAttribute("error", "Пароль обязателен");
            return "register";
        }
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("error", "Пароли не совпадают");
            return "register";
        }

        if (userAccountRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Пользователь с таким логином уже существует");
            return "register";
        }

        UserAccount saved = userAccountRepository.save(UserAccount.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .build());

        userRoleRepository.save(UserRole.builder()
                .userId(saved.getId())
                .role("ROLE_USER")
                .build());

        return "redirect:/login";
    }
}
