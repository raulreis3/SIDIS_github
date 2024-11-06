package com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB;



import lombok.Getter;

import java.time.Month;

@Getter
public class LendingAvgPerMonthAndGenreDB {
    String year;
    String month;
    String genre;
    double averageDuration;

    public LendingAvgPerMonthAndGenreDB(int year, int month, String genre, double AverageDuration) {
        this.year = String.valueOf(year);
        this.month = String.valueOf(Month.of(month));
        this.genre = genre;
        this.averageDuration = AverageDuration;
    }
}


