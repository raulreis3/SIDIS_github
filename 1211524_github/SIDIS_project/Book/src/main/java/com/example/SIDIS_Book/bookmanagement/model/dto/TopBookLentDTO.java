package com.example.SIDIS_Book.bookmanagement.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopBookLentDTO {

    private SimplifiedBookDTO book;
    private int numberOfTimesLent;

    public TopBookLentDTO(SimplifiedBookDTO book, int numberOfTimesLent) {
        this.book = book;
        this.numberOfTimesLent = numberOfTimesLent;
    }
}
