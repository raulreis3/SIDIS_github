package com.example.SIDIS_Book.authormanagement.repositories;

import com.example.SIDIS_Book.authormanagement.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(:name || '%')")
    List<Author> findByNameStartingWith(@Param("name") String name);

    @Query("SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(:name)")
    Optional<Author> findByName(@Param("name") String name);

    Author getAuthorById(Long authorId);

}
