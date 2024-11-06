package com.example.SIDIS_Lending.lendingmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class LendingPage {

    private long totalResults;

    private int page;

    private int pageResults;

    private List<Lending> lendings;
}
