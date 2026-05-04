package com.example.spring.bpmresolver.services;

import com.example.spring.bpmresolver.dto.LoginRequest;
import com.example.spring.bpmresolver.dto.LoginResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.jwt.Jwt;

public interface AuthService {

    FormLoginResult loginForm(String username, String password, String redirect);

    LoginResponse loginJson(LoginRequest request);

    LogoutResult logout(Jwt jwt);

    record FormLoginResult(int status, String location, ResponseCookie cookie) {
    }

    record LogoutResult(int status, String location, ResponseCookie cookie) {
    }
}
