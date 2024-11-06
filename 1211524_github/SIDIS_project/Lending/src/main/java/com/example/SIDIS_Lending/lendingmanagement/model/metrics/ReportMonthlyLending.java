package com.example.SIDIS_Lending.lendingmanagement.model.metrics;
import lombok.Getter;

@Getter
public class ReportMonthlyLending {
    private final String yearMonth;//2024/1
    private final Long monthlyLendings;
    private final Double MonthTotalAverage;
    public ReportMonthlyLending(String yearMonth, Long monthlyLendings, Double average) {
        this.yearMonth = yearMonth;
        this.monthlyLendings = monthlyLendings;
        this.MonthTotalAverage = average;
    }
}
