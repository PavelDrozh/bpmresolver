package com.example.spring.bpmresolver.localization;

import com.example.spring.bpmresolver.config.LocaleProvider;
import org.springframework.context.MessageSource;

public class LocalizationServiceImpl implements LocalizationService {

    public static final String FEIGN_FALLBACK_EXTERNAL_SERVICE_ERROR = "feign.fallback.external_service_error";
    public static final String FEIGN_FALLBACK_EXTRACT_STATUS_FAILED = "feign.fallback.extract_status_failed";
    public static final String FEIGN_FALLBACK_ERROR_IN_CLIENT = "feign.fallback.error_in_client";
    public static final String VALIDATION_SERVICE_ID_BLANK = "validation.service_id_blank";
    public static final String VALIDATION_CONTEXT_BLANK = "validation.context_blank";
    public static final String VALIDATION_INVALID_CONTEXT = "validation.invalid_context";
    public static final String VALIDATION_INVALID_SERVICE_ID = "validation.invalid_service_id";
    public static final String VALIDATION_SERVICE_ID_NOT_ALLOWED = "validation.service_id_not_allowed";
    public static final String AUTH_UNAUTHORIZED = "auth.unauthorized";
    public static final String BPM_FINISHED_PROCESS_NOT_FOUND_BY_ID = "bpm_finished_process.not_found_by_id";
    public static final String BPM_FINISHED_PROCESS_NOT_FOUND_BY_PROCESS_INSTANCE_ID = "bpm_finished_process.not_found_by_process_instance_id";
    public static final String QBPM_PLAYER_FALLBACK_GENERIC_ERROR = "qbpm.player.fallback.generic_error";
    public static final String QBPMCOCKPIT_INSTANCES_REQUEST_LOG = "qbpmcockpit.instances.request_log";
    public static final String QBPMCOCKPIT_INSTANCES_ACCESS_DENIED_MESSAGE = "qbpmcockpit.instances.access_denied_message";

    private final LocaleProvider localeProvider;
    private final MessageSource messageSource;

    public LocalizationServiceImpl(LocaleProvider localeProvider, MessageSource messageSource) {
        this.localeProvider = localeProvider;
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, localeProvider.getCurrent());
    }
}
