package com.example.SIDIS_Book.bootstrapping;

import com.example.SIDIS_Book.bookmanagement.genre.model.Genre;
import com.example.SIDIS_Book.bookmanagement.genre.repository.GenreRepository;
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
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Profile("bootstrap")
@Order(4)
public class GenreBootstrapper implements CommandLineRunner {

    private final GenreRepository genreRepo;

    @Override
    public void run(final String... args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("genres.txt"))))) {
            List<String> genres = reader.lines().toList();

            for (String genreName : genres) {
                if (genreRepo.findByGenreName(genreName).isEmpty()) {
                    Genre genre = new Genre(genreName);
                    genreRepo.save(genre);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
