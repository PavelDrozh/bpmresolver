package com.example.spring.bpmresolver.services.impl;

import com.example.spring.bpmresolver.config.QbpmcockpitProperties;
import com.example.spring.bpmresolver.entities.ResolverTokenUserSetting;
import com.example.spring.bpmresolver.repositories.ResolverTokenUserSettingRepository;
import com.example.spring.bpmresolver.services.ResolverTokenService;
import com.example.spring.bpmresolver.util.DataBaseUtil;
import com.example.spring.bpmresolver.util.StringUtil;
import com.example.spring.bpmresolver.util.UsersUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class ResolverTokenServiceImpl implements ResolverTokenService {

    private final AtomicReference<String> token;
    private final ResolverTokenUserSettingRepository tokenUserSettingRepository;

    public ResolverTokenServiceImpl(QbpmcockpitProperties properties,
                                   ResolverTokenUserSettingRepository tokenUserSettingRepository) {
        this.token = new AtomicReference<>(StringUtil.normalize(properties.getToken()));
        this.tokenUserSettingRepository = tokenUserSettingRepository;
    }

    @Override
    public String getToken() {
        String username = UsersUtil.getCurrentUsernameOrNull();
        if (username != null) {
            String fromDb = DataBaseUtil.getFromDb(
                    tokenUserSettingRepository.findByUsername(username),
                    ResolverTokenUserSetting::getToken
            );
            if (fromDb != null) {
                return fromDb;
            }
        }
        return token.get();
    }

    @Override
    @Transactional
    public void setToken(String newToken) {
        String normalized = StringUtil.normalize(newToken);

        String username = UsersUtil.getCurrentUsernameOrNull();
        if (username == null) {
            token.set(normalized);
            return;
        }

        if (normalized == null) {
            tokenUserSettingRepository.deleteByUsername(username);
            return;
        }

        ResolverTokenUserSetting setting = tokenUserSettingRepository.findByUsername(username)
                .orElseGet(() -> ResolverTokenUserSetting.builder().username(username).build());
        setting.setToken(normalized);
        tokenUserSettingRepository.save(setting);
    }
}
