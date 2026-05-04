package com.example.spring.bpmresolver.clients;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.example.spring.bpmresolver.localization.LocalizationServiceImpl.FEIGN_FALLBACK_ERROR_IN_CLIENT;
import static com.example.spring.bpmresolver.localization.LocalizationServiceImpl.FEIGN_FALLBACK_EXTRACT_STATUS_FAILED;

@Slf4j
public final class FeignFallbackUtils {

    private FeignFallbackUtils() {
    }

    private static String getLocalizedMessage(String key, Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
        String pattern = bundle.getString(key);
        return MessageFormat.format(pattern, args);
    }

    public static int extractStatus(Throwable cause) {
        if (cause instanceof FeignException feignException) {
            return feignException.status();
        }
        log.error(getLocalizedMessage(FEIGN_FALLBACK_EXTRACT_STATUS_FAILED, cause.getMessage()));
        return -1;
    }

    public static String buildErrorMessage(String clientName, int status) {
        return getLocalizedMessage(FEIGN_FALLBACK_ERROR_IN_CLIENT, clientName, status);
    }
}
