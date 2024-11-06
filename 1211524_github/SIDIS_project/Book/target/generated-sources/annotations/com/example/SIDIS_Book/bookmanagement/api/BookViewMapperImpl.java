package com.example.SIDIS_Book.bookmanagement.api;

import com.example.SIDIS_Book.bookmanagement.model.Book;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-06T14:31:54+0000",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class BookViewMapperImpl extends BookViewMapper {

    @Override
    public BookView toBookView(Book book) {
        if ( book == null ) {
            return null;
        }

        BookView bookView = new BookView();

        bookView.setIsbn( book.getIsbn() );
        bookView.setTitle( book.getTitle() );
        bookView.setGenre( mapGenreToString( book.getGenre() ) );
        bookView.setDescription( mapOptString( book.getDescription() ) );
        bookView.setAuthors( mapAuthorsToAuthorViews( book.getAuthors() ) );
        bookView.setCoverUrl( book.getCoverUrl() );

        return bookView;
    }

    @Override
    public Iterable<BookView> toBookView(Iterable<Book> books) {
        if ( books == null ) {
            return null;
        }

        ArrayList<BookView> iterable = new ArrayList<BookView>();
        for ( Book book : books ) {
            iterable.add( toBookView( book ) );
        }

        return iterable;
    }

    @Override
    public List<BookView> toBookView(List<Book> books) {
        if ( books == null ) {
            return null;
        }

        List<BookView> list = new ArrayList<BookView>( books.size() );
        for ( Book book : books ) {
            list.add( toBookView( book ) );
        }

        return list;
    }
}
