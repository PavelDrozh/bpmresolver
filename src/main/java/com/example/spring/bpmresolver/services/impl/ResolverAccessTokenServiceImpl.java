package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.config.AuthAccessProperties;
import com.example.spring.bpmresolver.dto.TokenRequestDto;
import com.example.spring.bpmresolver.entities.ResolverAccessUserSetting;
import com.example.spring.bpmresolver.repositories.ResolverAccessUserSettingRepository;
import com.example.spring.bpmresolver.services.ResolverAccessTokenService;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
public class ResolverAccessTokenServiceImpl implements ResolverAccessTokenService {

    private final ResolverAccessUserSettingRepository userSettingRepository;
    private final AuthAccessProperties properties;

    public ResolverAccessTokenServiceImpl(AuthAccessProperties properties,
                                         ResolverAccessUserSettingRepository userSettingRepository) {
        this.userSettingRepository = userSettingRepository;
        this.properties = properties;
    }

    @Override
    public String getUrl() {
        String username = ServicesUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = ServicesUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    ResolverAccessUserSetting::getUrl
            );
            if (fromDb != null) {
                return fromDb;
            }
        }

        return ServicesUtil.normalize(properties.getUrl());
    }

    @Override
    public String getClientId() {
        String username = ServicesUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = ServicesUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    ResolverAccessUserSetting::getClientId
            );
            if (fromDb != null) {
                return fromDb;
            }
        }

        return ServicesUtil.normalize(properties.getClientId());
    }

    @Override
    public String getUserName() {
        String username = ServicesUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = ServicesUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    ResolverAccessUserSetting::getAccessUser
            );
            if (fromDb != null) {
                return fromDb;
            }
        }

        return ServicesUtil.normalize(properties.getUserName());
    }

    @Override
    public String getRealm() {
        String username = ServicesUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = ServicesUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    ResolverAccessUserSetting::getRealm
            );
            if (fromDb != null) {
                return fromDb;
            }
        }

        return ServicesUtil.normalize(properties.getRealm());
    }

    @Override
    @Transactional
    public void updateAccessSettings(TokenRequestDto settings) {
        if (settings == null) {
            return;
        }

        String username = ServicesUtil.getCurrentUsernameOrNull();
        if (username == null) {
            return;
        }

        ResolverAccessUserSetting setting = userSettingRepository.findByUsername(username)
                .orElseGet(() -> ResolverAccessUserSetting.builder().username(username).build());

        setting.setRealm(ServicesUtil.normalize(settings.getRealm()));
        setting.setClientId(ServicesUtil.normalize(settings.getClientId()));
        setting.setAccessUser(ServicesUtil.normalize(settings.getUserName()));
        setting.setUrl(ServicesUtil.normalize(settings.getUrl()));

        if (ServicesUtil.isBlank(setting.getRealm())
                && ServicesUtil.isBlank(setting.getClientId())
                && ServicesUtil.isBlank(setting.getAccessUser())
                && ServicesUtil.isBlank(setting.getUrl())) {
            userSettingRepository.deleteByUsername(username);
            return;
        }

        userSettingRepository.save(setting);
    }
}
