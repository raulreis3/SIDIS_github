package com.example.SIDIS_Lending.lendingmanagement.model.metrics.averages;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class YearMonthGroupAverage {
    String year;
    List<MonthGroupAverage> monthGroupAverages;
}
