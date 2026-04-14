package com.example.spring.bpmresolver.clients;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class FeignFallbackUtils {

    private FeignFallbackUtils() {
    }

    public static int extractStatus(Throwable cause) {
        if (cause instanceof FeignException feignException) {
            return feignException.status();
        }
        log.error("Failed to extract status from cause: {}", cause.getMessage());
        return -1;
    }

    public static String buildErrorMessage(String clientName, int status) {
        return "External service error in " + clientName + ", status=" + status;
    }
}
