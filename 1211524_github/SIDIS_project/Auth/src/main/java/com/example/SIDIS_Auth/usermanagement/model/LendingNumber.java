package com.example.SIDIS_Auth.usermanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class LendingNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer sequenceNumber;
    private String currentYear;

    public LendingNumber (int sequenceNumber) {
        this.sequenceNumber = sequenceNumber + 1;
        currentYear = String.valueOf(LocalDate.now().getYear());
    }
    public LendingNumber () {
        this.sequenceNumber = 1;
        currentYear = String.valueOf(LocalDate.now().getYear());
    }

    public String getLendingNumber () {
        return currentYear + "/" + sequenceNumber;
    }
}
