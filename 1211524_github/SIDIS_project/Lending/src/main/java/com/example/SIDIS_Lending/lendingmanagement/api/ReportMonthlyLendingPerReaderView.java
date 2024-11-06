package com.example.SIDIS_Lending.lendingmanagement.api;

import com.example.SIDIS_Lending.lendingmanagement.model.metrics.ReportMonthlyLending;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(description = "Monthly Lendings Report Per Reader")
@NoArgsConstructor
@AllArgsConstructor
public class ReportMonthlyLendingPerReaderView {
    private String readerNumber;
    private List<ReportMonthlyLending> reports;
}
