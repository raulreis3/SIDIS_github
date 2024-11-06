package com.example.SIDIS_Lending.lendingmanagement.services;

import com.example.SIDIS_Lending.bookmanagement.model.Book;
import com.example.SIDIS_Lending.lendingmanagement.model.Lending;
import com.example.SIDIS_Lending.readermanagement.model.Reader;
import org.springframework.stereotype.Component;

@Component
public class LendingMapper  {

    public Lending create(Reader reader, Book book) {
        if ( reader == null || book == null) {
            return null;
        }
        return new Lending(reader, book);
    }

    public Lending create(Reader reader, Book book, Integer sequenceNumber) {
        if ( reader == null || book == null || sequenceNumber == null) {
            return null;
        }
        return new Lending(reader, book, sequenceNumber);
    }
}
