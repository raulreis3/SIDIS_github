package com.example.SIDIS_Book.authormanagement.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Author details")
public class AuthorView {

    @Schema(description = "The name of the author")
    private String name;

    @Schema(description = "The URL of the author's details")
    private String url;
}
