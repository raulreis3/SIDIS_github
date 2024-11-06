package com.example.SIDIS_Reader.readermanagement.repositories;

import com.example.SIDIS_Reader.readermanagement.model.ReaderPfp;

public interface ReaderPfpRepository {
    ReaderPfp save(ReaderPfp obj);

    ReaderPfp getByReaderNumber(final String readerNumber);
}
