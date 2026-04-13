package com.example.spring.bpmresolver.controllers.view;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.IntStream;

public class ViewUtil {

    private ViewUtil() {
        /* This utility class should not be instantiated */
    }

    public static List<Integer> getPageNumbers(Page<?> result, int page) {
        int totalPages = Math.max(result.getTotalPages(), 1);
        int current = Math.min(Math.max(page, 0), totalPages - 1);
        int fromPage = Math.max(0, current - 3);
        int toPage = Math.min(totalPages - 1, current + 3);
        return IntStream.rangeClosed(fromPage, toPage).boxed().toList();
    }
}
