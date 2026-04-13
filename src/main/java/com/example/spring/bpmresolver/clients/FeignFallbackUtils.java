package com.example.spring.bpmresolver.clients;

import feign.FeignException;

public final class FeignFallbackUtils {

    private FeignFallbackUtils() {
    }

    public static int extractStatus(Throwable cause) {
        if (cause instanceof FeignException feignException) {
            return feignException.status();
        }
        return -1;
    }

    public static String buildErrorMessage(String clientName, int status) {
        return "External service error in " + clientName + ", status=" + status;
    }
}
