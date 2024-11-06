package com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB;

import lombok.Getter;
import lombok.Setter;

import java.time.Month;

@Setter
@Getter
public class LendingReportDB {

    private Month month;
    private String genre;
    private long numberOfLendings;

    public LendingReportDB(int month, String genre, long numberOfLendings) {
        this.month = Month.of(month);
        this.genre = genre;
        this.numberOfLendings = numberOfLendings;
    }
}