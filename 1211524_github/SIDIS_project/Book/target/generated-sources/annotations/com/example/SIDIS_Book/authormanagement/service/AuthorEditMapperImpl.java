package com.example.SIDIS_Book.authormanagement.service;

import com.example.SIDIS_Book.authormanagement.model.Author;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-11-06T14:31:54+0000",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class AuthorEditMapperImpl extends AuthorEditMapper {

    @Override
    public Author create(CreateAuthorRequest request) {
        if ( request == null ) {
            return null;
        }

        Author author = new Author();

        author.setName( request.getName() );
        author.setShortBio( request.getShortBio() );

        return author;
    }
}
