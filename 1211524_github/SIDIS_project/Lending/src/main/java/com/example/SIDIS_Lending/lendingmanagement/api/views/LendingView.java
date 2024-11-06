package com.example.SIDIS_Lending.lendingmanagement.api.views;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;


@Data
@Schema(description = "Lending")
public class LendingView {

    private boolean isActive;

    private String lendingNumber;

    private String bookTitle;

    private LocalDate lendingDate;

    private LocalDate lendDeadline;

    private Long daysUntilDeadline;

    private Long daysInOverdue;

    private String fine;

    private String comment;
}
