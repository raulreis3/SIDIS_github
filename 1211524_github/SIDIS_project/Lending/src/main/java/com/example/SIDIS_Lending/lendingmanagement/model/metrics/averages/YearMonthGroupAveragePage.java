package com.example.SIDIS_Lending.lendingmanagement.model.metrics.averages;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class YearMonthGroupAveragePage {

    private long totalResults;

    private int page;

    private int pageResults;

    private List<YearMonthGroupAverage> yearMonthGroupAverages;
}
