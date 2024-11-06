package com.example.SIDIS_Lending.lendingmanagement.utils;

import java.time.Year;

public class YearValidator {
    private static final int MIN_YEAR = 2022;
    private static final int MAX_YEAR = Year.now().getValue();

    public static boolean isValidYear(Integer year) {
        return year != null && year >= MIN_YEAR && year <= MAX_YEAR;
    }
}
