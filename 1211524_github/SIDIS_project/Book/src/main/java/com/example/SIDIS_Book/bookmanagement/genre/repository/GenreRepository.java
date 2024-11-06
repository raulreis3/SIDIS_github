package com.example.SIDIS_Book.bookmanagement.genre.repository;
import com.example.SIDIS_Book.bookmanagement.genre.model.Genre;

import java.util.Optional;


public interface GenreRepository{

    Genre save(final Genre genre);
    Optional<Genre> findByGenreName(String genreName);

    default Genre getByGenreName(final String genreName) {
        final Optional<Genre> maybeGenre = findByGenreName(genreName);
        return maybeGenre.get();
    }


    default Optional<Genre> getByGenreNameForValidation(final String genreName) {
        final Optional<Genre> maybeGenre = findByGenreName(genreName);
        return maybeGenre;
    }
    Iterable<Genre> findAll();
}
