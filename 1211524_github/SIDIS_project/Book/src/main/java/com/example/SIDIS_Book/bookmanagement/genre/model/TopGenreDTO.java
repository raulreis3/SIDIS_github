package com.example.SIDIS_Book.bookmanagement.genre.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TopGenreDTO {

    private String genre;
    private int numberOfBooks;

    public TopGenreDTO(String genre, int numberOfBooks) {
        this.genre = genre;
        this.numberOfBooks = numberOfBooks;
    }
}