package com.example.SIDIS_Lending.lendingmanagement.model.metrics.averages;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupAverage {
    private String group;
    private Average average;

    public GroupAverage(String group, Average average) {
        this.group = group;
        this.average = average;
    }
}
