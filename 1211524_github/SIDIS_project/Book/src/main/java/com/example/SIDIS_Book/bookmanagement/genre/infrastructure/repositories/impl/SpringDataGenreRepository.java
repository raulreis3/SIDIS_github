package com.example.SIDIS_Book.bookmanagement.genre.infrastructure.repositories.impl;

import com.example.SIDIS_Book.bookmanagement.genre.repository.GenreRepository;
import com.example.SIDIS_Book.bookmanagement.genre.model.Genre;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SpringDataGenreRepository  extends GenreRepository, CrudRepository<Genre, Long> {

    @Cacheable
    Optional<Genre> findByGenreName(String genreName);

    @Cacheable
    default Genre getByGenreName(final String genreName) {
        final Optional<Genre> maybeGenre = findByGenreName(genreName);
        return maybeGenre.get();
    }

    @Cacheable
    default Optional<Genre> getByGenreNameForValidation(final String genreName) {
        final Optional<Genre> maybeGenre = findByGenreName(genreName);
        return maybeGenre;
    }

}
