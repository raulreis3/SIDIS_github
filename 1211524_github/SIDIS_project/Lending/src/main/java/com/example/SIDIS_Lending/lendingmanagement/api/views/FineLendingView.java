package com.example.SIDIS_Lending.lendingmanagement.api.views;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;


@Data
@Schema(description = "Overdue lending")
//View used for returning overdue book
public class FineLendingView {

    private LocalDate lendingDate;

    private LocalDate lendDeadline;

    private Long daysInOverdue;

    private String fine;
}
