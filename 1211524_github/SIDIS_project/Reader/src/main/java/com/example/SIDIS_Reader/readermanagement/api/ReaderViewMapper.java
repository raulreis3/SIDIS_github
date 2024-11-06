package com.example.SIDIS_Reader.readermanagement.api;

import com.example.SIDIS_Reader.readermanagement.model.Reader;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ReaderViewMapper {
    public abstract ReaderView toReaderView(Reader reader);

    public abstract List<ReaderView> toReaderView(List<Reader> readers);

}
