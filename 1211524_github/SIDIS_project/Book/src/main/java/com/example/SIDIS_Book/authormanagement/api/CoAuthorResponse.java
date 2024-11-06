package com.example.SIDIS_Book.authormanagement.api;

import com.example.SIDIS_Book.authormanagement.model.Author;
import com.example.SIDIS_Book.authormanagement.service.BookResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CoAuthorResponse {
    @Setter
    @Getter
    private Author coAuthor;
    @Setter
    @Getter

    private List<BookResponse> books;

}
