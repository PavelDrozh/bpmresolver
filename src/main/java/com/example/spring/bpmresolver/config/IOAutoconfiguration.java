package com.example.spring.bpmresolver.config;

import com.example.spring.bpmresolver.locilization.LocalizationService;
import com.example.spring.bpmresolver.locilization.LocalizationServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(MessageSource.class)
@Configuration
public class IOAutoconfiguration {

    @ConditionalOnMissingBean(LocaleProvider.class)
    @Bean
    public LocaleProvider localeProvider(@Value("${spring.application.locale:en-US}") String localeTag) {
        return new DefaultLocaleProvider(localeTag);
    }

    @ConditionalOnMissingBean(LocalizationService.class)
    @Bean
    public LocalizationService localizationService(LocaleProvider localeProvider,
                                                   MessageSource messageSource) {
        return new LocalizationServiceImpl(localeProvider, messageSource);
    }

}
