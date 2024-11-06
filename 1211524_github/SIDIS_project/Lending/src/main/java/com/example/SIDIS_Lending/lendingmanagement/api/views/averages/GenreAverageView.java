package com.example.SIDIS_Lending.lendingmanagement.api.views.averages;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class GenreAverageView {
    private String genre;
    private AverageView average;
}
