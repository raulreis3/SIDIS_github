package com.example.SIDIS_Lending.lendingmanagement.model.metrics;

import lombok.Getter;

import java.util.List;

@Getter
public class ReportMonthlyLendingPerReader {
    private final String readerNumber;
    private final List<ReportMonthlyLending> reports;

    public ReportMonthlyLendingPerReader(String readerNumber, List<ReportMonthlyLending> reports) {
        this.readerNumber = readerNumber;
        this.reports = reports;
    }
}
