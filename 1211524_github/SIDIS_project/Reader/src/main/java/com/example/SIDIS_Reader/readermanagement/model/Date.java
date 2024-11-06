package com.example.SIDIS_Reader.readermanagement.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Date {
    String dateFormat = "dd/MM/yyyy";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

    public boolean validate(final String date){
        try {
            LocalDate thisDate = LocalDate.parse(date, formatter);
        }catch(Exception e){
            return false;
        }
        return true;
    }
}
