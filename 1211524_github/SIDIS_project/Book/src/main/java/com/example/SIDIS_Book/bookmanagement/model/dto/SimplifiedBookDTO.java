package com.example.SIDIS_Book.bookmanagement.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimplifiedBookDTO {
    private String isbn;
    private String title;
    private String coverUrl;

    public SimplifiedBookDTO(String isbn, String title, String coverUrl) {
        this.isbn = isbn;
        this.title = title;
        this.coverUrl = coverUrl;
    }
}
