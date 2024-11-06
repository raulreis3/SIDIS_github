package com.example.SIDIS_Book.authormanagement.service;

import com.example.SIDIS_Book.authormanagement.api.CoAuthorResponse;
import com.example.SIDIS_Book.authormanagement.api.PhotoResponse;
import com.example.SIDIS_Book.authormanagement.api.TopAuthor;
import com.example.SIDIS_Book.authormanagement.api.TopReaderPerGenreDTO;
import com.example.SIDIS_Book.authormanagement.model.Author;
import com.example.SIDIS_Book.authormanagement.repositories.AuthorRepository;
import com.example.SIDIS_Book.bookmanagement.model.Book;
import com.example.SIDIS_Book.bookmanagement.repositories.BookRepository;
import com.example.SIDIS_Book.bookmanagement.utils.CoverUrlUtil;
import com.example.SIDIS_Book.exceptions.NotFoundException;
import com.example.SIDIS_Book.authormanagement.model.Reader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorEditMapper mapper;

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper ();

    @Override
    public Author createAuthor(CreateAuthorRequest request, MultipartFile photo) {
        Author author = mapper.create(request);
        Author savedAuthor = authorRepository.save(author);
        Long authorId = savedAuthor.getId();
        if (photo != null && photo.getSize() > 0) {
            PhotoResponse photoResponse = savePhoto(photo, authorId);
            savedAuthor.setPhotoPath(photoResponse.getPhotoPath());
            savedAuthor.setPhotoURL(photoResponse.getPhotoURL());
            authorRepository.save(savedAuthor);
        }
        return savedAuthor;
    }

    @Override
    public Author updateAuthor(Long authorId, UpdateAuthorRequest request, MultipartFile photo) {
        Author existingAuthor = authorRepository.findById(authorId).orElse(null);
        if (existingAuthor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
        }
        if (request.getName()!= null &&!request.getName().isEmpty()) {
            existingAuthor.setName(request.getName());
        }
        if (request.getShortBio()!= null &&!request.getShortBio().isEmpty()) {
            existingAuthor.setShortBio(request.getShortBio());
        }
        if (photo != null && photo.getSize() > 0) {
            PhotoResponse photoResponse = savePhoto(photo, authorId);
            existingAuthor.setPhotoPath(photoResponse.getPhotoPath());
            existingAuthor.setPhotoURL(photoResponse.getPhotoURL());
        }
        return authorRepository.save(existingAuthor);
    }

    @Override
    public ResponseEntity<Author> getAuthorById(Long authorId) {
        Author author = authorRepository.findById(authorId).orElse(null);
        if (author == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
        } else {
            return new ResponseEntity<>(author, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<List<Author>> searchAuthorsByName(String name) {
        List<Author> authors = authorRepository.findByNameStartingWith(name);
        if (authors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
        } else {
            return new ResponseEntity<>(authors, HttpStatus.OK);
        }
    }

    @Override
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Override
    public List<Book> getBooksByAuthorName(String authorName, int page, int size) {
        Author author = mapStringToAuthor(authorName);
        if (author == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found");
        }
        List<Book> books = bookRepository.findByAuthors(author);
        books.forEach(book -> {
            book.getAuthors().remove(author);
            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                book.setCoverUrl(CoverUrlUtil.generateSimplifiedCoverUrl(book.getIsbn()));
            }
        });

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, books.size());
        return books.subList(startIndex, endIndex);
    }

    public List<CoAuthorResponse> getCoAuthorsAndBooks(String authorName, int page, int size) {
        List<Book> books = bookRepository.findCoAuthorsAndBooks(authorName);
        List<CoAuthorResponse> responses = new ArrayList<>();

        for (Book book : books) {
            for (Author coAuthor : book.getAuthors()) {
                if (!coAuthor.getName().equals(authorName)) {
                    CoAuthorResponse response = responses.stream()
                            .filter(r -> r.getCoAuthor().getId().equals(coAuthor.getId()))
                            .findFirst()
                            .orElseGet(() -> {
                                CoAuthorResponse newResponse = new CoAuthorResponse();
                                newResponse.setCoAuthor(coAuthor);
                                newResponse.setBooks(new ArrayList<>());
                                responses.add(newResponse);
                                return newResponse;
                            });

                    BookResponse bookResponse = new BookResponse();
                    bookResponse.setIsbn(book.getIsbn());
                    bookResponse.setTitle(book.getTitle());
                    bookResponse.setGenre(book.getGenre().getGenreName());
                    bookResponse.setDescription(book.getDescription().orElse(null));
                    bookResponse.setCoverUrl(book.getCoverUrl());

                    response.getBooks().add(bookResponse);
                }
            }
        }

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, responses.size());
        return responses.subList(startIndex, endIndex);
    }

    public Optional<List<Author>> getTopAuthors() throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI("http://localhost:8081/api/lendings/topauthors");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            JsonNode authorsNode = objectMapper.readTree(responseBody);
            List<Author> topAuthors = objectMapper.convertValue(authorsNode, new TypeReference<List<Author>>() {});
            return Optional.of(topAuthors);
        } else {
            return Optional.empty();
        }
    }

    @SneakyThrows
    private PhotoResponse savePhoto(MultipartFile photo, long authorId) {

        String currentDir = new File("").getAbsolutePath();
        String teste = currentDir + "/src/main/resources/uploads/";

        // Create the uploads directory if it doesn't exist
        File dir = new File(teste);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate a unique filename for the photo
        String filename = UUID.randomUUID().toString() + "." + photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf(".") + 1);

        // Save the photo to the uploads directory
        File file = new File(teste + filename);
        photo.transferTo(file);

        String photoURL = "http://localhost:8080/api/" + "authors/" + authorId + "/authorPicture";
        String photoPath = "src/main/resources/uploads/"+ filename;

        return new PhotoResponse(photoURL, photoPath);
    }

    @Override
    public byte[] getAuthorPic(Long authorId) {
        Author author = authorRepository.getAuthorById(authorId);
        File file = new File(author.getPhotoPath());

        try {
            byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Optional<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }

    public Author mapStringToAuthor(String authorName) {
        if (authorName == null || authorName.isEmpty()) {
            return null;
        }
        return authorRepository.findByName(authorName)
                .orElseThrow(() -> new NotFoundException("Author not found: " + authorName));
    }
}
