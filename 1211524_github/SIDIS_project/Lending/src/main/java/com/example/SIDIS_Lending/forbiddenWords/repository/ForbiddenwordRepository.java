package com.example.SIDIS_Lending.forbiddenWords.repository;

import com.example.SIDIS_Lending.forbiddenWords.model.Forbiddenword;

public interface ForbiddenwordRepository {
    Forbiddenword save(Forbiddenword f);
    int findForbiddenword(String fw);

    boolean isForbiddenword(String name);
}
