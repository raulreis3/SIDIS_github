package com.example.SIDIS_Lending.lendingmanagement.repositories;

import com.example.SIDIS_Lending.lendingmanagement.model.Lending;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingAvgPerBookDB;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingAvgPerMonthAndGenreDB;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingCountPerGenreDB;
import com.example.SIDIS_Lending.lendingmanagement.model.metrics.answerDB.LendingReportDB;
import com.example.SIDIS_Lending.readermanagement.model.Reader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LendingRepository {
    Optional<Lending> findByLendingNumber(String year, Integer sequenceNumber);

    List<Lending> findByIsbn(String isbn);

    Optional<Lending> findActiveByIsbn(String isbn);

    Set<Lending> findActiveByReaderNumber(String readerNumber);

    Lending save(Lending lending);

    Integer findMaxLendingNumber();

    Page<Lending> findAll(Pageable pageable);

    Optional<Lending> findById(Long id);

    List<Reader> getTopReaders();

    Set<String> getTopGenres();

    List<Object[]> getTopAuthors();

    List<Object[]> getTopReadersPerGenre(@Param("genre") String genre, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Page<Lending> findActiveLendingsOrderByTardiness(Long maxLoanDays, LocalDate currentDate, Pageable pageable);

    List<Object[]> getTopBooksLent(Pageable pageable);

    List<LendingReportDB> findLendingCountsByMonthAndGenre(LocalDate startDate);

    List<String> getDatesAfterCertainDate(LocalDate startDate, LocalDate endDate);

    Long getLendingsMonthReader(LocalDate startDate, LocalDate endDate, String readerNumber);
    Long getLendingsMonth(LocalDate startDate, LocalDate endDate);
    Long getReadersMonth(LocalDate startDate, LocalDate endDate);

    double getAverageDuration();

    Page<LendingAvgPerBookDB> getAverageDurationPerBook(Pageable pageable);

    Page<LendingCountPerGenreDB> findLendingCountsPerGenreForMonth(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<LendingAvgPerMonthAndGenreDB> getAverageDurationPerMonthPerGenre(LocalDate startDate, LocalDate endDate, Pageable pageable);


}
