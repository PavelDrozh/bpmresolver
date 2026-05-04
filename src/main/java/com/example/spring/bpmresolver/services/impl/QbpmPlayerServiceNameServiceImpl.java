package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.config.QbpmPlayerProperties;
import com.example.spring.bpmresolver.clients.ServiceIdValidator;
import com.example.spring.bpmresolver.entities.QbpmplayerUserSetting;
import com.example.spring.bpmresolver.repositories.QbpmplayerUserSettingRepository;
import com.example.spring.bpmresolver.services.QbpmPlayerServiceNameService;
import com.example.spring.bpmresolver.util.DataBaseUtil;
import com.example.spring.bpmresolver.util.StringUtil;
import com.example.spring.bpmresolver.util.UsersUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QbpmPlayerServiceNameServiceImpl implements QbpmPlayerServiceNameService {

    private final QbpmplayerUserSettingRepository userSettingRepository;
    private final QbpmPlayerProperties properties;
    private final ServiceIdValidator serviceIdValidator;

    public QbpmPlayerServiceNameServiceImpl(QbpmplayerUserSettingRepository userSettingRepository,
                                            QbpmPlayerProperties properties,
                                            ServiceIdValidator serviceIdValidator) {
        this.userSettingRepository = userSettingRepository;
        this.properties = properties;
        this.serviceIdValidator = serviceIdValidator;
    }

    @Override
    public String getServiceNameOrNull() {
        String username = UsersUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = DataBaseUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    QbpmplayerUserSetting::getBaseUrl
            );
            if (fromDb != null) {
                return fromDb;
            }
        }

        String defaultService = properties.getService();
        if (defaultService == null) {
            return null;
        }
        defaultService = defaultService.trim();
        return defaultService.isBlank() ? null : defaultService;
    }

    @Override
    public String getContextOrNull() {
        String username = UsersUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = DataBaseUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    QbpmplayerUserSetting::getContext
            );
            if (fromDb != null) {
                return fromDb;
            }
        }

        String defaultContext = properties.getContext();
        if (defaultContext == null) {
            return null;
        }
        defaultContext = defaultContext.trim();
        return defaultContext.isBlank() ? null : defaultContext;
    }

    @Override
    @Transactional
    public void setServiceName(String baseUrl) {
        String username = UsersUtil.getCurrentUsernameOrNull();
        if (username == null) {
            return;
        }

        String normalized = getNormalized(baseUrl);
        if (normalized != null) {
            serviceIdValidator.validateQbpmplayerServiceIdOrThrow(normalized);
        }

        QbpmplayerUserSetting setting = userSettingRepository.findByUsername(username)
                .orElseGet(() -> QbpmplayerUserSetting.builder().username(username).build());
        setting.setBaseUrl(normalized);

        if (StringUtil.isBlank(setting.getBaseUrl()) && StringUtil.isBlank(setting.getContext())) {
            userSettingRepository.deleteByUsername(username);
            return;
        }

        userSettingRepository.save(setting);
    }

    @Override
    @Transactional
    public void setContext(String context) {
        String username = UsersUtil.getCurrentUsernameOrNull();
        if (username == null) {
            return;
        }
        String normalized = getNormalized(context);
        if (normalized != null) {
            serviceIdValidator.validateContextOrThrow(normalized);
        }

        QbpmplayerUserSetting setting = userSettingRepository.findByUsername(username)
                .orElseGet(() -> QbpmplayerUserSetting.builder().username(username).build());
        setting.setContext(normalized);

        if (StringUtil.isBlank(setting.getBaseUrl()) && StringUtil.isBlank(setting.getContext())) {
            userSettingRepository.deleteByUsername(username);
            return;
        }

        userSettingRepository.save(setting);
    }

    private static String getNormalized(String context) {
        String normalized = (context == null) ? null : context.trim();
        if (normalized != null && normalized.isBlank()) {
            normalized = null;
        }
        return normalized;
    }

}
