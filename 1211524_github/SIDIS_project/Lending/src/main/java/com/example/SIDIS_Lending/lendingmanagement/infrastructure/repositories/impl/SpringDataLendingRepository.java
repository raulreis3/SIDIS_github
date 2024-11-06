package com.example.SIDIS_Lending.lendingmanagement.infrastructure.repositories.impl;

import com.example.SIDIS_Lending.lendingmanagement.model.Lending;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingAvgPerBookDB;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingAvgPerMonthAndGenreDB;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingCountPerGenreDB;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingReportDB;
import com.example.SIDIS_Lending.lendingmanagement.repositories.LendingRepository;
import com.example.SIDIS_Lending.readermanagement.model.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SpringDataLendingRepository extends LendingRepository, CrudRepository<Lending, Long> {

    @Override
    @Query("SELECT l FROM Lending l WHERE l.lendingNumber.currentYear = :year AND l.lendingNumber.sequenceNumber = :number")
    Optional<Lending> findByLendingNumber(@Param("year") String year, @Param("number") Integer sequenceNumber);


    @Override
    @Query("SELECT l FROM Lending l WHERE l.reader.readerNumber = :readerNumber " +
            "AND l.isActive = true")
    Set<Lending> findActiveByReaderNumber(@Param("readerNumber") String readerNumber);

    @Override
    @Query("SELECT MAX(l.lendingNumber.sequenceNumber) FROM Lending l")
    Integer findMaxLendingNumber();

    @Override
    @Query("SELECT l FROM Lending l WHERE l.book.isbn = :isbn")
    List<Lending> findByIsbn(@Param("isbn") String isbn);

    @Override
    @Query("SELECT l FROM Lending l WHERE l.book.isbn = :isbn AND l.isActive = true")
    Optional<Lending> findActiveByIsbn(@Param("isbn") String isbn);

    @Override
    @Query("SELECT l.reader FROM Lending l GROUP BY l.reader.readerNumber ORDER BY COUNT(*) DESC LIMIT 5")
    List<Reader> getTopReaders();

    @Override
    @Query("SELECT l.book.genre FROM Lending l GROUP BY l.book.genre.genreName ORDER BY COUNT(*) DESC LIMIT 5")
    Set<String> getTopGenres();

    @Query("SELECT a, COUNT(l) AS lendingsCount FROM Lending l JOIN l.book b JOIN b.authors a GROUP BY a ORDER BY lendingsCount DESC LIMIT 5")
    List<Object[]> getTopAuthors();

    @Query("SELECT r, COUNT(l) AS lendingsCount " +
            "FROM Lending l JOIN l.reader r JOIN l.book b " +
            "WHERE b.genre.genreName = :genre AND l.lendingDate BETWEEN :startDate AND :endDate " +
            "GROUP BY r.readerNumber ORDER BY lendingsCount DESC")
    List<Object[]> getTopReadersPerGenre(@Param("genre") String genre, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("SELECT l FROM Lending l WHERE l.isActive = true " +
            "AND DATEDIFF(day, l.lendingDate, :currentDate) > :maxLoanDays " +
            "ORDER BY DATEDIFF(day, l.lendingDate, :currentDate) DESC")
    Page<Lending> findActiveLendingsOrderByTardiness(@Param("maxLoanDays") Long maxLoanDays, @Param("currentDate") LocalDate currentDate, Pageable pageable);

    @Query(value = "SELECT b.isbn, COUNT(l.id) as cnt FROM Lending l JOIN Book b ON l.book_id = b.pk WHERE l.lending_date > DATEADD('DAY', -365, CURRENT_DATE) GROUP BY b.isbn ORDER BY cnt DESC", nativeQuery = true)
    List<Object[]> getTopBooksLent(Pageable pageable);

    @Query("SELECT new com.project.psoft.lendingmanagement.model.metrics.answerDB.LendingReportDB(MONTH(l.lendingDate), b.genre.genreName, COUNT(l)) " +
            "FROM Lending l JOIN l.book b " +
            "WHERE l.lendingDate >= :startDate " +
            "GROUP BY MONTH(l.lendingDate), b.genre.genreName " +
            "ORDER BY MONTH(l.lendingDate), b.genre.genreName")
    List<LendingReportDB> findLendingCountsByMonthAndGenre(@Param("startDate") LocalDate startDate);

    @Override
    @Query("SELECT DISTINCT YEAR(l.lendingDate), MONTH(l.lendingDate) FROM Lending l WHERE l.lendingDate BETWEEN :startDate AND :endDate")
    List<String> getDatesAfterCertainDate(LocalDate startDate, LocalDate endDate);

    @Override
    @Query("SELECT COUNT(*) FROM Lending l WHERE l.lendingDate BETWEEN :startDate AND :endDate AND l.reader.readerNumber LIKE :readerNumber")
    Long getLendingsMonthReader(LocalDate startDate, LocalDate endDate, String readerNumber);

    @Override
    @Query("SELECT COUNT(*) FROM Lending l WHERE l.lendingDate BETWEEN :startDate AND :endDate")
    Long getLendingsMonth(LocalDate startDate, LocalDate endDate);

    @Override
    @Query("SELECT COUNT(DISTINCT l.reader.readerNumber) FROM Lending l WHERE l.lendingDate BETWEEN :startDate AND :endDate")
    Long getReadersMonth(LocalDate startDate, LocalDate endDate);

    @Override
    @Query("SELECT new com.project.psoft.lendingmanagement.model.metrics.answerDB.LendingCountPerGenreDB(l.book.genre.genreName, COUNT(l)) " +
            "FROM Lending l " +
            "WHERE l.lendingDate BETWEEN :startDate AND :endDate " +
            "GROUP BY l.book.genre.genreName")
    Page<LendingCountPerGenreDB> findLendingCountsPerGenreForMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    @Override
    @Query("SELECT AVG(DATEDIFF(day, l.lendingDate, l.returnDate)) AS averageDuration " +
            "FROM Lending l " +
            "WHERE l.isActive = false ")
    double getAverageDuration();

    @Override
    @Query("SELECT new com.project.psoft.lendingmanagement.model.metrics.answerDB.LendingAvgPerBookDB((l.book.title), " +
            "AVG(DATEDIFF(day, l.lendingDate, l.returnDate))) AS averageDuration " +
            "FROM Lending l " +
            "WHERE l.isActive = false " +
            "GROUP BY l.book.title")
    Page<LendingAvgPerBookDB> getAverageDurationPerBook(Pageable pageable);

    @Override
    @Query("SELECT new com.project.psoft.lendingmanagement.model.metrics.answerDB.LendingAvgPerMonthAndGenreDB (YEAR(l.lendingDate), " +
            "MONTH(l.lendingDate), " +
            "l.book.genre.genreName, " +
            "AVG(DATEDIFF(day, l.lendingDate, l.returnDate))) " +
            "FROM Lending l " +
            "WHERE l.isActive = false " +
            "AND l.lendingDate BETWEEN :startDate AND :endDate " +
            "AND l.returnDate BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(l.lendingDate), MONTH(l.lendingDate), l.book.genre.genreName")
    Page<LendingAvgPerMonthAndGenreDB> getAverageDurationPerMonthPerGenre(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

}
