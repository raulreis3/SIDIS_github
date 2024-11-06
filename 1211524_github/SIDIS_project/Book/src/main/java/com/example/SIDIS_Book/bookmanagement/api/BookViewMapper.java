package com.example.SIDIS_Book.bookmanagement.api;

import com.example.SIDIS_Book.authormanagement.api.AuthorView;
import com.example.SIDIS_Book.authormanagement.model.Author;
import com.example.SIDIS_Book.bookmanagement.model.Book;
import com.example.SIDIS_Book.bookmanagement.genre.model.Genre;
import org.mapstruct.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class BookViewMapper {

    @Mapping(source = "isbn", target = "isbn")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "genre", target = "genre", qualifiedByName = "mapGenreToString")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "authors", target = "authors", qualifiedByName = "mapAuthorsToAuthorViews")
    @Mapping(source = "coverUrl", target = "coverUrl")
    public abstract BookView toBookView(Book book);

    public abstract Iterable<BookView> toBookView(Iterable<Book> books);

    public abstract List<BookView> toBookView(List<Book> books);

    public String mapOptString(final Optional<String> i) {
        return i.orElse(null);
    }

    @Named("mapAuthorsToAuthorViews")
    protected List<AuthorView> mapAuthorsToAuthorViews(List<Author> authors) {
        return authors.stream()
                .map(author -> new AuthorView(
                        author.getName(),
                        ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/authors/{id}")
                                .buildAndExpand(author.getId())
                                .toUriString()
                ))
                .collect(Collectors.toList());
    }

    @Named("mapGenreToString")
    protected String mapGenreToString(Genre genre) {
        return genre.getGenreName();
    }
}