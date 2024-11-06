package com.example.SIDIS_Book.bootstrapping;

import com.example.SIDIS_Book.bookmanagement.repositories.BookRepository;
import com.example.SIDIS_Book.bookmanagement.services.BookService;
import com.example.SIDIS_Book.bookmanagement.services.EditBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Spring will load and execute all components that implement the interface
 * CommandLineRunner on startup, so we will use that as a way to bootstrap some
 * data for testing purposes.
 * <p>
 * In order to enable this bootstraping make sure you activate the spring
 * profile "bootstrap" in application.properties
 */
@Component
@RequiredArgsConstructor
@Profile("bootstrap")
@Order(5)
public class BookBootstrapper implements CommandLineRunner {

    private final BookRepository bookRepo;
    private final BookService bookService;

    @Override
    public void run(final String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("books.txt"))))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header line
                    continue;
                }
                String[] fields = line.split(";");
                if (fields.length >= 6) {
                    String isbn = fields[0];
                    String title = fields[1];
                    String genre = fields[2];
                    String description = fields[3].isEmpty() ? null : fields[3];
                    List<String> authorNames = fields[4].isEmpty() ? List.of() : Arrays.asList(fields[4].split(","));
                    String coverPath = fields[5].isEmpty() ? null : fields[5];
                    createBookIfNotExists(isbn, title, genre, description, authorNames, coverPath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createBookIfNotExists(String isbn, String title, String genre, String description, List<String> authorNames, String coverPath) {
        if (bookRepo.findByIsbn(isbn).isEmpty()) {
            MultipartFile coverFile = coverPath != null ? convertToMultipartFile(coverPath) : null;
            EditBookRequest resource = new EditBookRequest();
            resource.setTitle(title);
            resource.setGenre(genre);
            resource.setDescription(description);
            resource.setAuthors(authorNames);
            resource.setCoverFile(coverFile);
            bookService.create(isbn, resource);
        }
    }

    private MultipartFile convertToMultipartFile(String filePath) {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            try {
                byte[] content = Files.readAllBytes(path);
                String mimeType = Files.probeContentType(path);
                return new MockMultipartFile("file", path.getFileName().toString(), mimeType, content);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }
}
