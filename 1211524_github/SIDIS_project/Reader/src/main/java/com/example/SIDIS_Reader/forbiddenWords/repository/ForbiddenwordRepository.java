package com.example.SIDIS_Reader.forbiddenWords.repository;

import com.example.SIDIS_Reader.forbiddenWords.model.Forbiddenword;

public interface ForbiddenwordRepository {
    Forbiddenword save(Forbiddenword f);
    int findForbiddenword(String fw);

    boolean isForbiddenword(String name);
}
