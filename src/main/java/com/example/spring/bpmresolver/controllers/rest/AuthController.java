package com.example.spring.bpmresolver.controllers.rest;

import com.example.spring.bpmresolver.dto.LoginRequest;
import com.example.spring.bpmresolver.dto.LoginResponse;
import com.example.spring.bpmresolver.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void loginForm(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "redirect", required = false) String redirect,
            HttpServletResponse response
    ) {
        AuthService.FormLoginResult result = authService.loginForm(username, password, redirect);
        if (result.cookie() != null) {
            response.addHeader("Set-Cookie", result.cookie().toString());
        }
        response.setStatus(result.status());
        response.setHeader("Location", result.location());
    }

    @PostMapping(value = "/login-json", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse loginJson(@RequestBody LoginRequest request) {
        return authService.loginJson(request);
    }

    @PostMapping(value = "/logout")
    public void logout(
            @AuthenticationPrincipal Jwt jwt,
            HttpServletResponse response
    ) {
        AuthService.LogoutResult result = authService.logout(jwt);
        response.addHeader("Set-Cookie", result.cookie().toString());
        response.setStatus(result.status());
        response.setHeader("Location", result.location());
    }
}
