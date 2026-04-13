package com.example.spring.bpmresolver.locilization;


import com.example.spring.bpmresolver.config.LocaleProvider;
import org.springframework.context.MessageSource;

public class LocalizationServiceImpl implements LocalizationService {

    public static final String BOOKS_TOTAL = "books.total";


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
