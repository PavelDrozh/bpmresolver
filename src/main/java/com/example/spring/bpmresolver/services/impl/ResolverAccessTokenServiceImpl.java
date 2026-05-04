package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.config.AuthAccessProperties;
import com.example.spring.bpmresolver.dto.TokenRequestDto;
import com.example.spring.bpmresolver.entities.ResolverAccessUserSetting;
import com.example.spring.bpmresolver.repositories.ResolverAccessUserSettingRepository;
import com.example.spring.bpmresolver.services.ResolverAccessTokenService;
import com.example.spring.bpmresolver.util.DataBaseUtil;
import com.example.spring.bpmresolver.util.StringUtil;
import com.example.spring.bpmresolver.util.UsersUtil;
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
        String username = UsersUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = DataBaseUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    ResolverAccessUserSetting::getUrl
            );
            if (fromDb != null) {
                return fromDb;
            }
        }

        return StringUtil.normalize(properties.getUrl());
    }

    @Override
    public String getClientId() {
        String username = UsersUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = DataBaseUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    ResolverAccessUserSetting::getClientId
            );
            if (fromDb != null) {
                return fromDb;
            }
        }

        return StringUtil.normalize(properties.getClientId());
    }

    @Override
    public String getUserName() {
        String username = UsersUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = DataBaseUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    ResolverAccessUserSetting::getAccessUser
            );
            if (fromDb != null) {
                return fromDb;
            }
        }

        return StringUtil.normalize(properties.getUserName());
    }

    @Override
    public String getRealm() {
        String username = UsersUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = DataBaseUtil.getFromDb(
                    userSettingRepository.findByUsername(username),
                    ResolverAccessUserSetting::getRealm
            );
            if (fromDb != null) {
                return fromDb;
            }
        }

        return StringUtil.normalize(properties.getRealm());
    }

    @Override
    @Transactional
    public void updateAccessSettings(TokenRequestDto settings) {
        if (settings == null) {
            return;
        }

        String username = UsersUtil.getCurrentUsernameOrNull();
        if (username == null) {
            return;
        }

        ResolverAccessUserSetting setting = userSettingRepository.findByUsername(username)
                .orElseGet(() -> ResolverAccessUserSetting.builder().username(username).build());

        setting.setRealm(StringUtil.normalize(settings.getRealm()));
        setting.setClientId(StringUtil.normalize(settings.getClientId()));
        setting.setAccessUser(StringUtil.normalize(settings.getUserName()));
        setting.setUrl(StringUtil.normalize(settings.getUrl()));

        if (StringUtil.isBlank(setting.getRealm())
                && StringUtil.isBlank(setting.getClientId())
                && StringUtil.isBlank(setting.getAccessUser())
                && StringUtil.isBlank(setting.getUrl())) {
            userSettingRepository.deleteByUsername(username);
            return;
        }

        userSettingRepository.save(setting);
    }
}
