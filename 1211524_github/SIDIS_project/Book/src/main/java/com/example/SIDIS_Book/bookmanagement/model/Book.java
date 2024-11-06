package com.example.SIDIS_Book.bookmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.SIDIS_Book.authormanagement.model.Author;
import com.example.SIDIS_Book.bookmanagement.genre.model.Genre;
import jakarta.persistence.*;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.StaleObjectStateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @JsonIgnore
    @Getter
    @Version
    private Long version;

    @Getter
    @Column(nullable = false, unique = true, updatable = false)
    @NotNull
    @NotBlank
    @Size(min = 10, max = 17)
    private String isbn;

    @Getter
    @Column(nullable = false)
    @NotNull
    @NotBlank
    @Size(max = 128, message = "Max of 128 characters")
    private String title;

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    @Getter
    @NotNull
    private Genre genre;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_isbn"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @Getter
    @NotNull
    private List<Author> authors = new ArrayList<>();

    @Getter
    @Setter
    @Column
    private String coverUrl;

    protected Book() {
        // for ORM only
    }

    public Book(final String isbn) {
        setIsbn(isbn);
    }

    public void setIsbn(final String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("'ISBN' is a mandatory attribute of Book");
        }
        ISBN isbnValidator = new ISBN(isbn);
        isbnValidator.validate();

        if (!isbnValidator.isValid()) {
            throw new IllegalArgumentException("Invalid ISBN format. Invalid property: " + isbnValidator.getInvalidProperty());
        }
        this.isbn = isbn;
    }

    public void setTitle(final String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is a mandatory attribute of Book");
        }
        if (title.trim().length() != title.length()) {
            throw new ConstraintViolationException("Title cannot start or end with whitespace", null);
        }
        if (title.length() > 128) {
            throw new ConstraintViolationException("Title: max of 128 characters", null);
        }
        this.title = title;
    }

    public void setGenre(final Genre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre is a mandatory attribute of Book");
        }
        this.genre = genre;
    }


    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public void setAuthors(final List<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            throw new IllegalArgumentException("At least one author is a mandatory attribute of Book");
        }
        this.authors = authors;
    }

    public void updateData(final long desiredVersion, final String title, final Genre genre, final String description, final List<Author> authors) {
        if (this.version != desiredVersion) {
            throw new StaleObjectStateException("Object was already modified by another user", this.pk);
        }
        setTitle(title);
        setGenre(genre);
        setDescription(description);
        setAuthors(authors);
    }

    public void applyPatch(final long desiredVersion, final String title, final Genre genre, final String description, final List<Author> authors) {
        if (this.version != desiredVersion) {
            throw new StaleObjectStateException("Object was already modified by another user", this.pk);
        }
        if (title != null) {
            setTitle(title);
        }
        if (genre != null) {
            setGenre(genre);
        }
        if (description != null) {
            setDescription(description);
        }
        if (authors != null) {
            setAuthors(authors);
        }
    }
}
