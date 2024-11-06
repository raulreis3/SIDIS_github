package com.example.SIDIS_Book.authormanagement.service;

import com.example.SIDIS_Book.authormanagement.api.CoAuthorResponse;
import com.example.SIDIS_Book.authormanagement.api.TopAuthor;
import com.example.SIDIS_Book.authormanagement.api.TopReaderPerGenreDTO;
import com.example.SIDIS_Book.authormanagement.model.Author;
import com.example.SIDIS_Book.bookmanagement.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AuthorService {

    //List<TopReaderPerGenreDTO> getTopReadersPerGenre(String genre, LocalDate startDate, LocalDate endDate);

    Author createAuthor(CreateAuthorRequest request, MultipartFile photo);

    Author updateAuthor(Long authorId, UpdateAuthorRequest request, MultipartFile photo);

    ResponseEntity<Author> getAuthorById(Long authorId);

    ResponseEntity<List<Author>> searchAuthorsByName(String name);

    List<Author> getAllAuthors();

    Optional<List<Author>> getTopAuthors() throws IOException, InterruptedException, URISyntaxException;

    byte[] getAuthorPic(Long authorId);

    List<Book> getBooksByAuthorName(String authorName, int page, int size);

    List<CoAuthorResponse> getCoAuthorsAndBooks(String authorName, int page, int size);

    Optional<Author> findByName(String name);

    Author mapStringToAuthor(String authorName);

}
