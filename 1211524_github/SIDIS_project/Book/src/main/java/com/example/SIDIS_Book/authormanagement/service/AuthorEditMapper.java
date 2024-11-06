package com.example.SIDIS_Book.authormanagement.service;

import com.example.SIDIS_Book.authormanagement.model.Author;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.ComponentScan;

@Mapper(componentModel = "spring")
@ComponentScan(basePackages = "com.example.SIDIS_Book")
public abstract class AuthorEditMapper {
    public abstract Author create(CreateAuthorRequest request);
}
