package com.example.SIDIS_Book.bookmanagement.api;

import com.example.SIDIS_Book.bookmanagement.model.Book;
import com.example.SIDIS_Book.bookmanagement.model.dto.TopBookLentDTO;
import com.example.SIDIS_Book.bookmanagement.services.BookService;
import com.example.SIDIS_Book.bookmanagement.services.EditBookRequest;
import com.example.SIDIS_Book.bookmanagement.services.FileStorageService;
import com.example.SIDIS_Book.bookmanagement.utils.CoverUrlUtil;
import com.example.SIDIS_Book.bookmanagement.utils.FileValidationUtil;
import com.example.SIDIS_Book.exceptions.NotFoundException;
import com.example.SIDIS_Book.bookmanagement.genre.model.TopGenreDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Books", description = "Endpoints for managing books")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
class BookController {
    private static final String IF_MATCH_HEADER = "If-Match";

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    private final BookService bookService;
    private final BookViewMapper bookViewMapper;
    private final FileStorageService fileStorageService;
    private final CoverUrlUtil coverUrlUtil;

    @Operation(summary = "Gets all books")
    @ApiResponse(description = "Success", responseCode = "200", content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = BookView.class)))})
    @GetMapping
    public ListResponse<BookView> findAllPaged(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int limit) {
        List<Book> books = bookService.findAll(page, limit);
        List<BookView> bookViews = bookViewMapper.toBookView(books);
        bookViews.forEach(bookView -> {
            if (bookView.getCoverUrl() != null && !bookView.getCoverUrl().isEmpty()) {
                bookView.setCoverUrl(CoverUrlUtil.generateSimplifiedCoverUrl(bookView.getIsbn()));
            }
        });

        long totalBooks = bookService.countBooks();
        int totalPages = (int) Math.ceil((double) totalBooks / limit);

        return new ListResponse<>(bookViews, page, totalPages, totalBooks);
    }

    @Operation(summary = "Gets a specific book")
    @GetMapping(value = "/{isbn}")
    public ResponseEntity<BookView> findByIsbn(
            @PathVariable("isbn") @Parameter(description = "The isbn of the book to find") final String isbn) {
        final var book = bookService.findOne(isbn).orElseThrow(() -> new NotFoundException(Book.class, isbn));
        BookView bookView = bookViewMapper.toBookView(book);
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            bookView.setCoverUrl(CoverUrlUtil.generateSimplifiedCoverUrl(book.getIsbn()));
        }
        return ResponseEntity.ok().eTag(Long.toString(book.getVersion())).body(bookView);
    }

    private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
        if (ifMatchHeader.startsWith("\"")) {
            return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
        }
        return Long.parseLong(ifMatchHeader);
    }

    @Operation(summary = "Creates or updates a book and optionally uploads a book cover")
    @PutMapping(value = "/{isbn}", consumes = "multipart/form-data")
    public ResponseEntity<BookView> upsert(final WebRequest request,
                                           @PathVariable("isbn") final String isbn,
                                           @Valid final EditBookRequest resource,
                                           @RequestPart(value = "cover", required = false) final MultipartFile coverFile) {
        final String ifMatchValue = request.getHeader(IF_MATCH_HEADER);

        FileValidationUtil.validateCoverFile(coverFile);
        if (coverFile != null) {
            resource.setCoverFile(coverFile);
        }

        Book book;
        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            book = bookService.create(isbn, resource);
            final var newBookUri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
            BookView bookView = bookViewMapper.toBookView(book);
            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                bookView.setCoverUrl(CoverUrlUtil.generateSimplifiedCoverUrl(book.getIsbn()));
            }
            return ResponseEntity.created(newBookUri)
                    .eTag(Long.toString(book.getVersion()))
                    .body(bookView);
        } else {
            book = bookService.update(isbn, resource, getVersionFromIfMatchHeader(ifMatchValue));
            BookView bookView = bookViewMapper.toBookView(book);
            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                bookView.setCoverUrl(CoverUrlUtil.generateSimplifiedCoverUrl(book.getIsbn()));
            }
            return ResponseEntity.ok()
                    .eTag(Long.toString(book.getVersion()))
                    .body(bookView);
        }
    }

    @Operation(summary = "Partially updates an existing book")
    @PatchMapping(value = "/{isbn}", consumes = "multipart/form-data")
    public ResponseEntity<BookView> partialUpdate(final WebRequest request,
                                                  @PathVariable("isbn") final String isbn,
                                                  @Parameter(description = "The isbn of the book to update")
                                                  @Valid final EditBookRequest resource,
                                                  @RequestPart(value = "cover", required = false) final MultipartFile coverFile) {
        final String ifMatchValue = request.getHeader(IF_MATCH_HEADER);

        FileValidationUtil.validateCoverFile(coverFile);
        if (coverFile != null) {
            resource.setCoverFile(coverFile);
        }

        if (ifMatchValue == null || ifMatchValue.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must issue a conditional PATCH using 'if-match'");
        }
        final var book = bookService.partialUpdate(isbn, resource, getVersionFromIfMatchHeader(ifMatchValue));
        BookView bookView = bookViewMapper.toBookView(book);
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            bookView.setCoverUrl(CoverUrlUtil.generateSimplifiedCoverUrl(book.getIsbn()));
        }
        return ResponseEntity.ok().eTag(Long.toString(book.getVersion())).body(bookView);
    }

    @Operation(summary = "Search books by title, genre, or author")
    @GetMapping(value = "/search")
    public ListResponse<BookView> searchBooks(@RequestParam Map<String, String> params,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int limit) {
        String title = params.getOrDefault("title", null);
        String genre = params.getOrDefault("genre", null);
        String author = params.getOrDefault("author", null);

        if ((title == null || title.isEmpty()) && (genre == null || genre.isEmpty()) && (author == null || author.isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid request parameters. At least one of 'title', 'genre', or 'author' parameter is required.");
        }

        List<Book> books;
        long totalBooks;

        if (title != null && !title.isEmpty()) {
            if (genre != null && !genre.isEmpty()) {
                books = bookService.searchByTitleAndGenre(title, genre, page, limit);
                totalBooks = bookService.countBooksByTitleAndGenre(title, genre);
            } else {
                books = bookService.searchTitle(title, page, limit);
                totalBooks = bookService.countBooksByTitle(title);
            }
        } else if (genre != null && !genre.isEmpty()) {
            books = bookService.searchGenre(genre, page, limit);
            totalBooks = bookService.countBooksByGenre(genre);
        } else {
            books = bookService.searchAuthor(author, page, limit);
            totalBooks = bookService.countBooksByAuthor(author);
        }

        List<BookView> bookViews = bookViewMapper.toBookView(books);
        bookViews.forEach(bookView -> {
            if (bookView.getCoverUrl() != null && !bookView.getCoverUrl().isEmpty()) {
                bookView.setCoverUrl(CoverUrlUtil.generateSimplifiedCoverUrl(bookView.getIsbn()));
            }
        });
        int totalPages = (int) Math.ceil((double) totalBooks / limit);
        return new ListResponse<>(bookViews, page, totalPages, totalBooks);
    }

    @Operation(summary = "Uploads a book cover")
    @PostMapping("/{isbn}/cover")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UploadFileResponse> uploadFile(@PathVariable("isbn") final String isbn,
                                                         @RequestParam("file") final MultipartFile file) throws URISyntaxException {
        final UploadFileResponse up = doUploadFile(isbn, file);
        return ResponseEntity.created(new URI(up.getFileDownloadUri())).body(up);
    }

    public UploadFileResponse doUploadFile(final String id, final MultipartFile file) {
        final String fileName = fileStorageService.storeFile(id, file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(fileName)
                .toUriString();
        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @Operation(summary = "Downloads a cover of a book")
    @GetMapping("/{isbn}/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable final String fileName,
                                                 final HttpServletRequest request) {
        final Resource resource = fileStorageService.loadFileAsResource(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (final IOException ex) {
            logger.info("Could not determine file type.");
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Operation(summary = "Downloads the cover of a book")
    @GetMapping(value = "/{isbn}/cover", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<Resource> downloadCover(@PathVariable("isbn") final String isbn) {
        final var book = bookService.findOne(isbn).orElseThrow(() -> new NotFoundException(Book.class, isbn));
        String coverUrl = book.getCoverUrl();
        if (coverUrl != null && !coverUrl.isEmpty()) {
            String[] parts = coverUrl.split("/");
            String coverFileName = parts[parts.length - 1];
            final Resource coverResource = fileStorageService.loadFileAsResource(coverFileName);
            if (coverResource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                        .body(coverResource);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Gets the top 5 genres")
    @GetMapping("/topgenres")
    public Map<String, TopGenreDTO> getTopGenres() {
        return bookService.getTopGenres();
    }

   @Operation(summary = "Gets the top 5 books lent")
   @GetMapping("TopBooks")
   public Optional<List<Book>> getTopBooksLent() throws IOException, URISyntaxException, InterruptedException {
    return bookService.getTopBooksLent();
    }
}