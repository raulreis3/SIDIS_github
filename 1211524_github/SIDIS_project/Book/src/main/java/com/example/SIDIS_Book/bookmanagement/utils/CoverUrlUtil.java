package com.example.SIDIS_Book.bookmanagement.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CoverUrlUtil {

    private static String baseUrl;
    private static String bookPath;

    public CoverUrlUtil(@Value("${app.base-url}") String baseUrl, @Value("${app.book-path}") String bookPath) {
        CoverUrlUtil.baseUrl = baseUrl;
        CoverUrlUtil.bookPath = bookPath;
    }

    public static String simplifiedUrl() {
        return baseUrl + bookPath;
    }

    public static String generateSimplifiedCoverUrl(String isbn) {
        return baseUrl + bookPath + isbn + "/cover";
    }
}
