package com.example.SIDIS_Book.bookmanagement.services;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A DTO for editing or creating a Book")
public class EditBookRequest {

    @Size(max = 128, message = "Max of 128 characters")
    @Schema(description = "The title of the book")
    private String title;

    @Schema(description = "The genre of the book")
    private String genre;

    @Schema(description = "A description of the book")
    private String description;

    @Schema(description = "The authors of the book")
    private List<String> authors;

    @Schema(description = "The URL of the book cover")
    private String coverUrl;

    @Schema(description = "The file of the book cover")
    private MultipartFile coverFile;
}
