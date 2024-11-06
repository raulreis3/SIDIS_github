package com.example.SIDIS_Book.authormanagement.api;

import com.example.SIDIS_Book.authormanagement.model.Author;
import com.example.SIDIS_Book.authormanagement.service.*;
import com.example.SIDIS_Book.bookmanagement.model.Book;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Operation(summary = "Create an Author")
    @PostMapping
    public ResponseEntity<Author> createAuthor(@Valid final CreateAuthorRequest request, @RequestPart("photo") MultipartFile photo) {
        return new ResponseEntity<>(authorService.createAuthor(request, photo), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an Author")
    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @Valid final UpdateAuthorRequest request, @RequestPart("photo") MultipartFile photo) {
        Author updatedAuthor = authorService.updateAuthor(id, request, photo);
        return ResponseEntity.ok(updatedAuthor);
    }

    @Operation(summary = "Get an Author by is Id")
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        return authorService.getAuthorById(id);
    }

    @Operation(summary = "Get all Authors")
    @GetMapping
    public ResponseEntity<List<Author>> searchAuthors(@RequestParam(name = "name", required = false) String name) {
        if (name == null) {
            return new ResponseEntity<>(authorService.getAllAuthors(), HttpStatus.OK);
        } else {
            return authorService.searchAuthorsByName(name);
        }
    }

    @Operation(summary = "Get an Author by is Name")
   @GetMapping("/books/{authorName}")
   public List<Book> getBooksByAuthorName(@PathVariable String authorName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
       return authorService.getBooksByAuthorName(authorName, page, size);
   }

    @Operation(summary = "Get the CoAuthor of an Author and their respective Books")
    @GetMapping("/coAuthors/{authorName}")
    public ResponseEntity<?> getCoAuthorsAndBooks(@PathVariable String authorName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<CoAuthorResponse> responses = authorService.getCoAuthorsAndBooks(authorName, page, size);
        if (responses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get the Top 5 authors(which have the most lent books)")
    @GetMapping("/TopAuthors")
    public Optional<List<Author>> getTopAuthors() throws IOException, InterruptedException, URISyntaxException {
        return authorService.getTopAuthors();
    }

    @Operation(summary = "Returns the Author profile picture")
    @GetMapping("{id}/authorPicture")
    public ResponseEntity<?> getAuthorPic(@PathVariable final Long id) {
        byte[] image = authorService.getAuthorPic(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(String.valueOf(MediaType.IMAGE_JPEG)))
                .body(image);
    }

/*    @Operation(summary = "Returns the Top 5 readers per genre of a certain period")
    @GetMapping("/top-readers-per-genre")
    public List<TopReaderPerGenreDTO> getTopReadersPerGenre(@RequestParam String genre, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return authorService.getTopReadersPerGenre(genre, startDate, endDate);
    }*/

}
