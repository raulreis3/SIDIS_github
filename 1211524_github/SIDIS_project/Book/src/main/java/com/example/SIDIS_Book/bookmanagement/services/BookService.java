package com.example.SIDIS_Book.bookmanagement.services;

import com.example.SIDIS_Book.bookmanagement.model.Book;
import com.example.SIDIS_Book.bookmanagement.model.dto.TopBookLentDTO;
import com.example.SIDIS_Book.bookmanagement.genre.model.TopGenreDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookService {

    List<Book> findAll(int page, int limit);

    long countBooks();

    Optional<Book> findOne(String isbn);

    Book create(String isbn, EditBookRequest resource);

    Book update(String isbn, EditBookRequest resource, long desiredVersion);

    Book partialUpdate(String isbn, EditBookRequest resource, long desiredVersion);

    List<Book> searchTitle(String title, int page, int limit);

    List<Book> searchAuthor(String author, int page, int limit);

    List<Book> searchGenre(String genre, int page, int limit);

    List<Book> searchByTitleAndGenre(String title, String genre, int page, int limit);

    long countBooksByTitle(String title);

    long countBooksByGenre(String genre);

    long countBooksByAuthor(String author);

    long countBooksByTitleAndGenre(String title, String genre);

    Map<String, TopGenreDTO> getTopGenres();

    Optional<List<Book>> getTopBooksLent ( ) throws IOException, InterruptedException, URISyntaxException;
}