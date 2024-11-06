package com.example.SIDIS_Book.authormanagement.api;

import com.example.SIDIS_Book.authormanagement.model.Author;

public class TopAuthor {
    private final Author author;
    private final Long lendingsCount;

    public TopAuthor(Author author, Long lendingsCount) {
        this.author = author;
        this.lendingsCount = lendingsCount;
    }

    public Author getAuthor() {
        return author;
    }

    public Long getLendingsCount() {
        return lendingsCount;
    }
}
