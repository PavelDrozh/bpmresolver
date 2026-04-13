package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.config.QbpmPlayerProperties;
import com.example.spring.bpmresolver.entities.QbpmplayerUserSetting;
import com.example.spring.bpmresolver.repositories.QbpmplayerUserSettingRepository;
import com.example.spring.bpmresolver.services.QbpmPlayerBaseUrlService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QbpmPlayerBaseUrlServiceImpl implements QbpmPlayerBaseUrlService {

    private final QbpmplayerUserSettingRepository userSettingRepository;
    private final QbpmPlayerProperties properties;

    public QbpmPlayerBaseUrlServiceImpl(QbpmplayerUserSettingRepository userSettingRepository,
                                       QbpmPlayerProperties properties) {
        this.userSettingRepository = userSettingRepository;
        this.properties = properties;
    }

    @Override
    public String getBaseUrlOrNull() {
        String username = ServicesUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = ServicesUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    QbpmplayerUserSetting::getBaseUrl
            );
            if (fromDb != null) {
                return fromDb;
            }
        }

        String defaultBaseUrl = properties.getBaseUrl();
        if (defaultBaseUrl == null) {
            return null;
        }
        defaultBaseUrl = defaultBaseUrl.trim();
        return defaultBaseUrl.isBlank() ? null : defaultBaseUrl;
    }

    @Override
    public String getContextOrNull() {
        String username = ServicesUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = ServicesUtil.getFromDb(
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
    public void setBaseUrl(String baseUrl) {
        String username = ServicesUtil.getCurrentUsernameOrNull();
        if (username == null) {
            return;
        }

        String normalized = getNormalized(baseUrl);

        QbpmplayerUserSetting setting = userSettingRepository.findByUsername(username)
                .orElseGet(() -> QbpmplayerUserSetting.builder().username(username).build());
        setting.setBaseUrl(normalized);

        if (ServicesUtil.isBlank(setting.getBaseUrl()) && ServicesUtil.isBlank(setting.getContext())) {
            userSettingRepository.deleteByUsername(username);
            return;
        }

        userSettingRepository.save(setting);
    }

    @Override
    @Transactional
    public void setContext(String context) {
        String username = ServicesUtil.getCurrentUsernameOrNull();
        if (username == null) {
            return;
        }
        String normalized = getNormalized(context);

        QbpmplayerUserSetting setting = userSettingRepository.findByUsername(username)
                .orElseGet(() -> QbpmplayerUserSetting.builder().username(username).build());
        setting.setContext(normalized);

        if (ServicesUtil.isBlank(setting.getBaseUrl()) && ServicesUtil.isBlank(setting.getContext())) {
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
