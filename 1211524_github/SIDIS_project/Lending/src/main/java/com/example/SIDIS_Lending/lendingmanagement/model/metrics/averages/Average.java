package com.example.SIDIS_Lending.lendingmanagement.model.metrics.averages;

import lombok.Getter;

@Getter
public class Average {
    double value;
    String unit;

    public Average (Long sum, int total, String unit) {
        double average= (double) sum / total;
        value = Math.round(average * 100.0) / 100.0;
        this.unit = unit;
    }

    public Average (double average, String unit) {
        value = Math.round(average * 100.0) / 100.0;
        this.unit = unit;
    }
}

