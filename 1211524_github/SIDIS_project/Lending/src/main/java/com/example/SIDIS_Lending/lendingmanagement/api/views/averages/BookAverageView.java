package com.example.SIDIS_Lending.lendingmanagement.api.views.averages;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class BookAverageView {
    private String book;
    private AverageView average;
}
