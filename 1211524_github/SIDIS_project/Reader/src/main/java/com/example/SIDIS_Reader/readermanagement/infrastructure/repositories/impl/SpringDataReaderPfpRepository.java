package com.example.SIDIS_Reader.readermanagement.infrastructure.repositories.impl;

import com.example.SIDIS_Reader.readermanagement.model.ReaderPfp;
import com.example.SIDIS_Reader.readermanagement.repositories.ReaderPfpRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "readersProfilePictures")
public interface SpringDataReaderPfpRepository extends ReaderPfpRepository, CrudRepository<ReaderPfp,Long> {
    @Override
    ReaderPfp save(ReaderPfp obj);

    @Override
    @Query("SELECT pp FROM ReaderPfp pp WHERE pp.reader.readerNumber LIKE :readerNumber")
    ReaderPfp getByReaderNumber(final String readerNumber);
}
