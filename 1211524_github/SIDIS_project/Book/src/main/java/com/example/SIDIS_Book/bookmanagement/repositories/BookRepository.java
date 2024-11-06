package com.example.SIDIS_Book.bookmanagement.repositories;

import com.example.SIDIS_Book.authormanagement.model.Author;
import com.example.SIDIS_Book.bookmanagement.model.Book;
import com.example.SIDIS_Book.bookmanagement.services.Page;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookRepoCustom {

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a IN (:author)")
    List<Book> findByAuthors(Author author);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.name = :authorName")
    List<Book> findCoAuthorsAndBooks(@Param("authorName") String authorName);

    Optional<Book> findByIsbn(String isbn);

    @Query(value = "SELECT * FROM Book b ORDER BY b.title LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Book> findAll(@Param("offset") int offset, @Param("limit") int limit);

    @Query(value = "SELECT * FROM Book b WHERE b.title LIKE %:title% ORDER BY b.title LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Book> searchTitle(@Param("title") String title, @Param("offset") int offset, @Param("limit") int limit);

    @Query(value = "SELECT b.* FROM Book b JOIN Genre g ON b.genre_id = g.id WHERE g.genre_name LIKE %:genre% ORDER BY b.title LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Book> searchGenre(@Param("genre") String genre, @Param("offset") int offset, @Param("limit") int limit);

    @Query(value = "SELECT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :author, '%')) ORDER BY b.title")
    List<Book> searchAuthor(@Param("author") String author, Pageable pageable);

    @Query(value = "SELECT b.* FROM Book b JOIN Genre g ON b.genre_id = g.id WHERE b.title LIKE %:title% AND g.genre_name LIKE %:genre% ORDER BY b.title LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Book> searchTitleAndGenre(@Param("title") String title, @Param("genre") String genre, @Param("offset") int offset, @Param("limit") int limit);

    @Query("SELECT b.genre.genreName, COUNT(b) as genre_count FROM Book b GROUP BY b.genre.genreName ORDER BY genre_count DESC, b.genre.genreName ASC")
    List<Object[]> findTopGenres(Pageable pageable);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.title LIKE %:title%")
    long countByTitle(@Param("title") String title);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.genre.genreName LIKE %:genre%")
    long countByGenre(@Param("genre") String genre);

    @Query("SELECT COUNT(b) FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :author, '%'))")
    long countByAuthor(@Param("author") String author);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.title LIKE %:title% AND b.genre.genreName LIKE %:genre%")
    long countByTitleAndGenre(@Param("title") String title, @Param("genre") String genre);

}

interface BookRepoCustom {
    List<Book> getBookSuggestions(Page page, Set<String> interestList);
}

@RequiredArgsConstructor
class BookRepoCustomImpl implements BookRepoCustom {

    // get the underlying JPA Entity Manager via spring thru constructor dependency
    // injection
    private final EntityManager em;

    @Override
    public List<Book> getBookSuggestions(Page page, Set<String> interestList) {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        final Root<Book> root = cq.from(Book.class);
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();

        for (String s : interestList) {
            if (StringUtils.hasText(s)) {
                where.add(cb.equal(root.get("genre").get("genreName"), s));
            }
        }

        // search using OR
        if (!where.isEmpty()) {
            cq.where(cb.or(where.toArray(new Predicate[0])));
        }

        //cq.orderBy(cb.desc(root.get("createdAt")));

        final TypedQuery<Book> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        return q.getResultList();
    }
}
