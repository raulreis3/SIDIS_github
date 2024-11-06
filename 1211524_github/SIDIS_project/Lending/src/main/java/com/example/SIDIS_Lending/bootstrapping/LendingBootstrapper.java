package com.example.SIDIS_Lending.bootstrapping;

import com.example.SIDIS_Lending.lendingmanagement.model.Book;
import com.example.SIDIS_Lending.lendingmanagement.repositories.BookRepository;
import com.example.SIDIS_Lending.exceptions.ConflictException;
import com.example.SIDIS_Lending.lendingmanagement.model.Lending;
import com.example.SIDIS_Lending.lendingmanagement.repositories.LendingRepository;
import com.example.SIDIS_Lending.lendingmanagement.model.Reader;
import com.example.SIDIS_Lending.lendingmanagement.repositories.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component()
@RequiredArgsConstructor
@Profile("bootstrap")
@Order(6)
public class LendingBootstrapper implements CommandLineRunner {

    private final LendingRepository lendingRepo;

    private final BookRepository bookRepo;

    private final ReaderRepository readerRepo;

    @Override
    public void run(final String... args) {
        List<Reader> readers = readerRepo.findAll();
        List<Book> books = (List<Book>) bookRepo.findAll();

        if (!readers.isEmpty() && !books.isEmpty()) {
            //------------ ACTIVE LENDINGS ------------//
            //Lending overdue for almost one year
            if (lendingRepo.findByLendingNumber("2024", 1).isEmpty()) {
                LocalDate customDate = LocalDate.of(2023, 5, 10);
                Reader reader = readers.get(0);
                checkReaderAvailability(reader);
                Book book = books.get(0);
                checkBookAvailability(book);
                final Lending lending1 = new Lending(reader, book, 0, customDate);
                lendingRepo.save(lending1);
            }

            //Lendings of reader 2024/2
            //It has two books lent. Can have 1 more book
            if (lendingRepo.findByLendingNumber("2024", 2).isEmpty()) {
                LocalDate customDate = LocalDate.of(2024, 4, 28);
                Reader reader = readers.get(1);
                checkReaderAvailability(reader);
                Book book = books.get(6);
                checkBookAvailability(book);

                final Lending lending2 = new Lending(reader, book, 1, customDate);
                lendingRepo.save(lending2);
            }

            if (lendingRepo.findByLendingNumber("2024", 3).isEmpty()) {
                LocalDate customDate = LocalDate.of(2024, 4, 28);
                Reader reader = readers.get(1);
                checkReaderAvailability(reader);
                Book book = books.get(4);
                checkBookAvailability(book);
                final Lending lending3 = new Lending(reader, book, 2, customDate);
                lendingRepo.save(lending3);
            }

            //Lendings of reader 2024/3
            //Has three lendings already, can't lend anymore
            if (lendingRepo.findByLendingNumber("2024", 4).isEmpty()) {
                LocalDate customDate = LocalDate.of(2024, 5, 4);
                Reader reader = readers.get(2);
                checkReaderAvailability(reader);
                Book book = books.get(7);
                checkBookAvailability(book);
                final Lending lending4 = new Lending(reader, book, 3, customDate);
                lendingRepo.save(lending4);
            }

            if (lendingRepo.findByLendingNumber("2024", 5).isEmpty()) {
                LocalDate customDate = LocalDate.of(2024, 5, 9);
                Reader reader = readers.get(2);
                checkReaderAvailability(reader);
                Book book = books.get(3);
                checkBookAvailability(book);
                final Lending lending5 = new Lending(reader, book, 4, customDate);
                lendingRepo.save(lending5);
            }

            if (lendingRepo.findByLendingNumber("2024", 6).isEmpty()) {
                LocalDate customDate = LocalDate.of(2024, 5, 12);
                Reader reader = readers.get(2);
                checkReaderAvailability(reader);
                Book book = books.get(5);
                checkBookAvailability(book);
                final Lending lending6 = new Lending(reader, book, 5, customDate);
                lendingRepo.save(lending6);
            }

            //Lendings of reader 2024/4
            if (lendingRepo.findByLendingNumber("2024", 7).isEmpty()) {
                LocalDate customDate = LocalDate.of(2024, 5, 10);
                Reader reader = readers.get(3);
                checkReaderAvailability(reader);
                Book book = books.get(9);
                checkBookAvailability(book);
                final Lending lending7 = new Lending(reader, book, 6, customDate);
                lendingRepo.save(lending7);
            }

            //Lendings of reader 2024/5
            //Has three lendings already, can't lend anymore
            if (lendingRepo.findByLendingNumber("2024", 8).isEmpty()) {
                LocalDate customDate = LocalDate.of(2003, 12, 10);
                Reader reader = readers.get(4);
                checkReaderAvailability(reader);
                Book book = books.get(1);
                checkBookAvailability(book);
                final Lending lending8 = new Lending(reader, book, 7, customDate);
                lendingRepo.save(lending8);
            }
            if (lendingRepo.findByLendingNumber("2024", 9).isEmpty()) {
                LocalDate customDate = LocalDate.of(2003, 12, 10);
                Reader reader = readers.get(4);
                checkReaderAvailability(reader);
                Book book = books.get(2);
                checkBookAvailability(book);
                final Lending lending9 = new Lending(reader, book, 8, customDate);
                lendingRepo.save(lending9);
            }
            if (lendingRepo.findByLendingNumber("2024", 10).isEmpty()) {
                LocalDate customDate = LocalDate.of(2003, 12, 10);
                Reader reader = readers.get(4);
                checkReaderAvailability(reader);
                Book book = books.get(8);
                checkBookAvailability(book);
                final Lending lending10 = new Lending(reader, book, 9, customDate);
                lendingRepo.save(lending10);
            }

            //------------ INACTIVE LENDINGS ------------//
            //Copy of active lendings, just modifying the date and
            //constructing with extra parameter (customReturnDate)
            //Economia
            if (lendingRepo.findByLendingNumber("2024", 11).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 2, 2);
                LocalDate customReturnDate = LocalDate.of(2024, 3, 2);
                Reader reader = readers.get(0);
                Book book = books.get(0);
                final Lending lending1 = new Lending(reader, book, 10, customLendingDate, customReturnDate);
                lendingRepo.save(lending1);
            }

            //Lendings of reader 2024/2
            //Romance
            if (lendingRepo.findByLendingNumber("2024", 12).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 1, 2);
                LocalDate customReturnDate = LocalDate.of(2024, 3, 2);
                Reader reader = readers.get(1);
                Book book = books.get(6);
                final Lending lending2 = new Lending(reader, book, 11, customLendingDate, customReturnDate);
                lendingRepo.save(lending2);
            }

            //Romance
            if (lendingRepo.findByLendingNumber("2024", 13).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 2, 2);
                LocalDate customReturnDate = LocalDate.of(2024, 3, 2);
                Reader reader = readers.get(1);
                Book book = books.get(4);
                final Lending lending3 = new Lending(reader, book, 12, customLendingDate, customReturnDate);
                lendingRepo.save(lending3);
            }

            //Lendings of reader 2024/3
            //Romance
            if (lendingRepo.findByLendingNumber("2024", 14).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 4, 2);
                LocalDate customReturnDate = LocalDate.of(2024, 5, 14);
                Reader reader = readers.get(2);
                Book book = books.get(7);
                final Lending lending4 = new Lending(reader, book, 13, customLendingDate, customReturnDate);
                lendingRepo.save(lending4);
            }

            //Sociologia
            if (lendingRepo.findByLendingNumber("2024", 15).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 1, 5);
                LocalDate customReturnDate = LocalDate.of(2024, 1, 14);
                Reader reader = readers.get(2);
                Book book = books.get(3);
                final Lending lending5 = new Lending(reader, book, 14, customLendingDate, customReturnDate);
                lendingRepo.save(lending5);
            }

            //Economia
            if (lendingRepo.findByLendingNumber("2024", 16).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 2, 22);
                LocalDate customReturnDate = LocalDate.of(2024, 2, 25);
                Reader reader = readers.get(2);
                Book book = books.get(5);
                final Lending lending6 = new Lending(reader, book, 15, customLendingDate, customReturnDate);
                lendingRepo.save(lending6);
            }

            //Lendings of reader 2024/4
            //Economia
            if (lendingRepo.findByLendingNumber("2024", 17).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 4, 7);
                LocalDate customReturnDate = LocalDate.of(2024, 4, 16);
                Reader reader = readers.get(3);
                Book book = books.get(9);
                final Lending lending7 = new Lending(reader, book, 16, customLendingDate, customReturnDate);
                lendingRepo.save(lending7);
            }

            //Lendings of reader 2024/5
            //Economia
            if (lendingRepo.findByLendingNumber("2024", 18).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 2, 13);
                LocalDate customReturnDate = LocalDate.of(2024, 5, 25);
                Reader reader = readers.get(4);
                Book book = books.get(1);
                final Lending lending8 = new Lending(reader, book, 17, customLendingDate, customReturnDate);
                lendingRepo.save(lending8);
            }

            //Romance
            if (lendingRepo.findByLendingNumber("2024", 19).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 4, 2);
                LocalDate customReturnDate = LocalDate.of(2024, 4, 10);
                Reader reader = readers.get(4);
                Book book = books.get(2);
                final Lending lending9 = new Lending(reader, book, 18, customLendingDate, customReturnDate);
                lendingRepo.save(lending9);
            }

            //Romance
            if (lendingRepo.findByLendingNumber("2024", 20).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 1, 25);
                LocalDate customReturnDate = LocalDate.of(2024, 3, 7);
                Reader reader = readers.get(4);
                Book book = books.get(8);
                final Lending lending10 = new Lending(reader, book, 19, customLendingDate, customReturnDate);
                lendingRepo.save(lending10);
            }

            if (lendingRepo.findByLendingNumber("2024", 21).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 3, 7);
                LocalDate customReturnDate = LocalDate.of(2024, 3, 17);
                Reader reader = readers.get(4);
                Book book = books.get(5);
                final Lending lending11 = new Lending(reader, book, 20, customLendingDate, customReturnDate);
                lendingRepo.save(lending11);
            }

            if (lendingRepo.findByLendingNumber("2024", 22).isEmpty()) {
                LocalDate customLendingDate = LocalDate.of(2024, 4, 7);
                LocalDate customReturnDate = LocalDate.of(2024, 4, 17);
                Reader reader = readers.get(5);
                Book book = books.get(5);
                final Lending lending12 = new Lending(reader, book, 21, customLendingDate, customReturnDate);
                lendingRepo.save(lending12);
            }

        }
    }
    private void checkBookAvailability(Book book) {
        Optional<Lending> activeLending = lendingRepo.findActiveByIsbn(book.getIsbn());
        if (activeLending.isPresent()) {
            throw new ConflictException("Book with ISBN <" + book.getIsbn() + "> is not available!");
        }
    }

    private void checkReaderAvailability (Reader reader) {
        Set<Lending> lendings = lendingRepo.findActiveByReaderNumber(reader.getReaderNumber());
        if (lendings.size() >= 3) {
            throw new ConflictException("Reader <" + reader.getReaderNumber() + "> has too many lendings!");
        }
    }
}
