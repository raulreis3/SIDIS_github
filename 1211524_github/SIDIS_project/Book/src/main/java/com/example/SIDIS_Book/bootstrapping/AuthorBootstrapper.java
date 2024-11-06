package com.example.SIDIS_Book.bootstrapping;

import com.example.SIDIS_Book.authormanagement.model.Author;
import com.example.SIDIS_Book.authormanagement.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
@Order(3)
public class AuthorBootstrapper implements CommandLineRunner {

    private final AuthorRepository authorRepo;

    @Override
    public void run(final String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("authors.txt"))))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header line
                    continue;
                }
                String[] fields = line.split(";");
                if (fields.length >= 2) {
                    String name = fields[0];
                    String shortBio = fields[1];
                    createAuthorIfNotExists(name, shortBio);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAuthorIfNotExists(String name, String shortBio) {
        if (authorRepo.findByName(name).isEmpty()) {
            Author author = new Author();
            author.setName(name);
            author.setShortBio(shortBio);
            authorRepo.save(author);
        }
    }
}
