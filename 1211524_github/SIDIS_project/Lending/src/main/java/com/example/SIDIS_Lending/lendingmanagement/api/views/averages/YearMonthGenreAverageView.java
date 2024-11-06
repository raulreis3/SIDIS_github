package com.example.SIDIS_Lending.lendingmanagement.api.views.averages;

import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Setter
public class YearMonthGenreAverageView {
    String year;
    List<MonthGenreAverageView> monthGenreAverages;
}
