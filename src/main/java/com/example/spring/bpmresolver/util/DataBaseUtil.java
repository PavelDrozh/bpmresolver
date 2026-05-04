package com.example.spring.bpmresolver.util;

import java.util.Optional;
import java.util.function.Function;

public class DataBaseUtil {

    private DataBaseUtil() {
        /* This utility class should not be instantiated */
    }

    public static <T> String getFromDb(Optional<T> optionalSetting, Function<T, String> getter) {
        if (optionalSetting.isEmpty()) {
            return null;
        }
        return optionalSetting
                .map(getter)
                .map(StringUtil::normalize)
                .orElse(null);
    }
}
