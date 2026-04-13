package com.example.spring.bpmresolver.dto;

public record LoginResponse(String accessToken, long expiresInSeconds) {
}
