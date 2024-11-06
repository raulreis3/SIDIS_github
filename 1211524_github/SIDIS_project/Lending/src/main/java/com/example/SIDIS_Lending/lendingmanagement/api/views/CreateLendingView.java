package com.example.SIDIS_Lending.lendingmanagement.api.views;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Lending created")
//view used for when the lending is created
public class CreateLendingView {

    private String lendingNumber;

    private String bookTitle;

    private LocalDate lendingDate;

    private LocalDate lendDeadline;

    private Long daysUntilDeadline;
}
