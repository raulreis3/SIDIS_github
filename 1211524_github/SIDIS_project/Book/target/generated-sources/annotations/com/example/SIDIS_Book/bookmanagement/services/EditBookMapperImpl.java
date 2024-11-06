package com.example.SIDIS_Book.bookmanagement.services;

import com.example.SIDIS_Book.authormanagement.model.Author;
import com.example.SIDIS_Book.bookmanagement.model.Book;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-06T14:31:54+0000",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class EditBookMapperImpl extends EditBookMapper {

    @Override
    public Book create(String isbn, EditBookRequest request) {
        if ( isbn == null && request == null ) {
            return null;
        }

        String isbn1 = null;
        isbn1 = isbn;

        Book book = new Book( isbn1 );

        if ( request != null ) {
            book.setTitle( request.getTitle() );
            book.setGenre( mapStringToGenre( request.getGenre() ) );
            book.setDescription( request.getDescription() );
            book.setAuthors( mapStringsToAuthors( request.getAuthors() ) );
            book.setCoverUrl( request.getCoverUrl() );
        }

        return book;
    }

    @Override
    public void update(String isbn, EditBookRequest request, Book book) {
        if ( isbn == null && request == null ) {
            return;
        }

        if ( request != null ) {
            book.setTitle( request.getTitle() );
            book.setGenre( mapStringToGenre( request.getGenre() ) );
            book.setDescription( request.getDescription() );
            if ( book.getAuthors() != null ) {
                List<Author> list = mapStringsToAuthors( request.getAuthors() );
                if ( list != null ) {
                    book.getAuthors().clear();
                    book.getAuthors().addAll( list );
                }
                else {
                    book.setAuthors( null );
                }
            }
            else {
                List<Author> list = mapStringsToAuthors( request.getAuthors() );
                if ( list != null ) {
                    book.setAuthors( list );
                }
            }
            book.setCoverUrl( request.getCoverUrl() );
        }
        book.setIsbn( isbn );
    }
}
