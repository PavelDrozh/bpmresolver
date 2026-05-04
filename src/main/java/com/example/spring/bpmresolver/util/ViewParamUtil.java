package com.example.spring.bpmresolver.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class ViewParamUtil {

    private ViewParamUtil() {
    }

    public static String blankToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public static LocalDateTime parseDateStart(String s) {
        String t = blankToNull(s);
        if (t == null) {
            return null;
        }
        try {
            LocalDate d = LocalDate.parse(t);
            return d.atStartOfDay();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static LocalDateTime parseDateEnd(String s) {
        String t = blankToNull(s);
        if (t == null) {
            return null;
        }
        try {
            LocalDate d = LocalDate.parse(t);
            return d.atTime(LocalTime.MAX);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
