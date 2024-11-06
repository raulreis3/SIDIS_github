package com.example.SIDIS_Lending.forbiddenWords.infrastructure.repositories.impl;

import com.example.SIDIS_Lending.forbiddenWords.model.Forbiddenword;
import com.example.SIDIS_Lending.forbiddenWords.repository.ForbiddenwordRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "forbiddenWords")
public interface SpringDataForbiddenwordRepository extends ForbiddenwordRepository, CrudRepository<Forbiddenword,Long>{
    @Override
    @Query("SELECT COUNT(fw) FROM Forbiddenword fw WHERE fw.word LIKE %:fw%")
    int findForbiddenword(String fw);

    default boolean isForbiddenword(String name){
        int num = 0;
        String[] splited = name.split(" ");
        for(int i=0; i<splited.length; i++)
        {
            num = num+findForbiddenword(splited[i].toLowerCase());
        }

        return num > 0;
    }

}
