package com.example.SIDIS_Book.bookmanagement.model;
import lombok.Setter;

public class Fine {

    @Setter
    static private float pricePerDay;

    static protected float calculate (long daysDifference) {
        return daysDifference * pricePerDay;
    }
}
