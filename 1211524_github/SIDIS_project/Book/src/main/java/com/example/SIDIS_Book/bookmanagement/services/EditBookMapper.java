package com.example.SIDIS_Book.bookmanagement.services;

import com.example.SIDIS_Book.authormanagement.model.Author;
import com.example.SIDIS_Book.authormanagement.repositories.AuthorRepository;
import com.example.SIDIS_Book.bookmanagement.model.Book;
import com.example.SIDIS_Book.exceptions.NotFoundException;
import com.example.SIDIS_Book.bookmanagement.genre.model.Genre;
import com.example.SIDIS_Book.bookmanagement.genre.repository.GenreRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class EditBookMapper {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Mapping(source = "request.title", target = "title")
    @Mapping(source = "request.genre", target = "genre", qualifiedByName = "mapStringToGenre")
    @Mapping(source = "request.description", target = "description")
    @Mapping(source = "request.authors", target = "authors", qualifiedByName = "mapStringsToAuthors")
    @Mapping(source = "request.coverUrl", target = "coverUrl")
    public abstract Book create(String isbn, EditBookRequest request);

    @Mapping(source = "request.title", target = "title")
    @Mapping(source = "request.genre", target = "genre", qualifiedByName = "mapStringToGenre")
    @Mapping(source = "request.description", target = "description")
    @Mapping(source = "request.authors", target = "authors", qualifiedByName = "mapStringsToAuthors")
    @Mapping(source = "request.coverUrl", target = "coverUrl")
    public abstract void update(String isbn, EditBookRequest request, @MappingTarget Book book);

    @Named("mapStringsToAuthors")
    protected List<Author> mapStringsToAuthors(List<String> authorNames) {
        return authorNames.stream()
                .map(this::mapStringToAuthor)
                .collect(Collectors.toList());
    }

    protected Author mapStringToAuthor(String authorName) {
        return authorRepository.findByName(authorName)
                .orElseThrow(() -> new NotFoundException("Author not found: " + authorName));
    }

    @Named("mapStringToGenre")
    protected Genre mapStringToGenre(String genreName) {
        return genreRepository.findByGenreName(genreName)
                .orElseThrow(() -> new NotFoundException("Genre not found: " + genreName));
    }
}