package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.config.QbpmcockpitProperties;
import com.example.spring.bpmresolver.entities.QbpmcockpitUserSetting;
import com.example.spring.bpmresolver.repositories.QbpmcockpitUserSettingRepository;
import com.example.spring.bpmresolver.services.QbpmcockpitBaseUrlService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QbpmcockpitBaseUrlServiceImpl implements QbpmcockpitBaseUrlService {

    private final QbpmcockpitUserSettingRepository userSettingRepository;
    private final QbpmcockpitProperties properties;

    public QbpmcockpitBaseUrlServiceImpl(QbpmcockpitUserSettingRepository userSettingRepository,
                                        QbpmcockpitProperties properties) {
        this.userSettingRepository = userSettingRepository;
        this.properties = properties;
    }

    @Override
    public String getBaseUrlOrNull() {
        String username = ServicesUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = ServicesUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    QbpmcockpitUserSetting::getBaseUrl
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
    @Transactional
    public void setBaseUrl(String baseUrl) {
        String username = ServicesUtil.getCurrentUsernameOrNull();
        if (username == null) {
            return;
        }

        String normalized = (baseUrl == null) ? null : baseUrl.trim();
        if (normalized == null || normalized.isBlank()) {
            userSettingRepository.deleteByUsername(username);
            return;
        }

        QbpmcockpitUserSetting setting = userSettingRepository.findByUsername(username)
                .orElseGet(() -> QbpmcockpitUserSetting.builder().username(username).build());
        setting.setBaseUrl(normalized);
        userSettingRepository.save(setting);
    }
}