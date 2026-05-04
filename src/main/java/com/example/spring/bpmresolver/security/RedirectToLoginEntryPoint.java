package com.example.spring.bpmresolver.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class RedirectToLoginEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        String fullPath = request.getRequestURI();
        if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
            fullPath = fullPath + "?" + request.getQueryString();
        }

        String location = UriComponentsBuilder.fromPath("/login")
                .queryParam("redirect", fullPath)
                .build()
                .toUriString();

        response.sendRedirect(location);
    }
}
