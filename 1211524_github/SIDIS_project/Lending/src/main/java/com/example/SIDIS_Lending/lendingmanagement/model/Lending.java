package com.example.SIDIS_Lending.lendingmanagement.model;

import com.example.SIDIS_Lending.bookmanagement.model.Book;
import com.example.SIDIS_Lending.readermanagement.model.Reader;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "Lending")
public class Lending {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Transient
    @Setter
    @Getter
    private static Long maxLoanDays;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "lending_id")
    private LendingNumber lendingNumber;

    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = false)
    @Getter
    private Reader reader;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    @Getter
    private Book book;

    @Column(nullable = false)
    @NotNull
    @Getter
    private LocalDate lendingDate;

    @Column(nullable = false)
    @NotNull
    @Getter
    private LocalDate lendDeadline;

    @Column
    @Getter
    private LocalDate returnDate;

    @Column()
    @Getter
    @NotNull
    private boolean isActive;

    @Column
    @Getter
    private Long daysUntilDeadline;

    @Column
    @Getter
    private Long daysInOverdue;

    @Column()
    @Getter
    private String comment;

    @Column()
    @Getter
    @Setter
    private Float fine;

    protected Lending() {
    }


    public Lending(final Reader reader, final Book book, final Integer sequenceNumber) {
        lendingNumber = new LendingNumber(sequenceNumber);
        this.book = book;
        this.reader = reader;
        lendingDate = LocalDate.now();
        isActive = true;
        lendDeadline = lendingDate.plusDays(maxLoanDays);
    }

    public Lending(final Reader reader, final Book book) {
        lendingNumber = new LendingNumber();
        this.book = book;
        this.reader = reader;
        lendingDate = LocalDate.now();
        isActive = true;
        lendDeadline = lendingDate.plusDays(maxLoanDays);
    }


    public String getLendingNumber() {
        return lendingNumber.getLendingNumber();
    }

    public void returnBook(String comment) {
        isActive = false;
        returnDate = LocalDate.now();
        this.comment = comment;
        update();
    }

    public void returnBook() {
        isActive = false;
        returnDate = LocalDate.now();
        update();
    }

    public boolean isOverdue() {
        long daysDifference = ChronoUnit.DAYS.between(lendingDate, LocalDate.now());
        return daysDifference > maxLoanDays;
    }

    public void update (){
        long daysDifference = ChronoUnit.DAYS.between(lendingDate, LocalDate.now());
        if (daysDifference > maxLoanDays) {
            daysInOverdue = daysDifference - maxLoanDays;
            fine = Fine.calculate(daysInOverdue);
        }
        else {
            daysUntilDeadline = maxLoanDays - daysDifference;
        }
    }


    //------- FOR BOOTSTRAPPING -----------//
    //Active lending
    public Lending(final Reader reader, final Book book, final Integer sequenceNumber, LocalDate customLendingDate) {
        lendingNumber = new LendingNumber(sequenceNumber);
        this.book = book;
        this.reader = reader;

        lendingDate = customLendingDate;
        this.isActive = true;
        lendDeadline = lendingDate.plusDays(maxLoanDays);
    }

    //Inactive Lending
    public Lending(final Reader reader, final Book book, final Integer sequenceNumber, LocalDate customLendingDate, LocalDate customReturnDate) {
        lendingNumber = new LendingNumber(sequenceNumber);
        this.book = book;
        this.reader = reader;
        lendingDate = customLendingDate;
        returnDate = customReturnDate;
        lendDeadline = lendingDate.plusDays(maxLoanDays);
        long daysDifference = ChronoUnit.DAYS.between(customLendingDate, customReturnDate);
        if (daysDifference > maxLoanDays) {
            daysInOverdue = daysDifference - maxLoanDays;
            fine = Fine.calculate(daysInOverdue);
        }
        else {
            daysUntilDeadline = maxLoanDays - daysDifference;
        }
        this.isActive = false;
    }
}

