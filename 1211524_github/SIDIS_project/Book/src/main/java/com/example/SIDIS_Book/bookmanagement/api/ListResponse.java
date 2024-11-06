package com.example.SIDIS_Book.bookmanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListResponse<T> {
    private List<T> books;
    private int page;
    private int totalPages;
    private long totalBooks;
}