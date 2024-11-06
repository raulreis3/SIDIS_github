package com.example.SIDIS_Lending.lendingmanagement.api.views;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "List of lendings")
public class LendingPageView {

    private long totalResults;

    private int page;

    private int pageResults;

    private List<LendingView> lendings;
}
