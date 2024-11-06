package com.example.SIDIS_Book.bookmanagement.api;

import com.example.SIDIS_Book.authormanagement.api.AuthorView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "A Book")
public class BookView {

    @Schema(description = "The ISBN of the book")
    private String isbn;

    @Schema(description = "The title of the book")
    private String title;

    @Schema(description = "The genre of the book")
    private String genre;

    @Schema(description = "A description of the book")
    private String description;

    @Schema(description = "The author of the book")
    private List<AuthorView> authors;

    @Schema(description = "The URL of the book cover")
    private String coverUrl;
}