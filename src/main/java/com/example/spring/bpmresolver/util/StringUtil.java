package com.example.spring.bpmresolver.util;

public class StringUtil {

    private StringUtil() {
        /* This utility class should not be instantiated */
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

}
