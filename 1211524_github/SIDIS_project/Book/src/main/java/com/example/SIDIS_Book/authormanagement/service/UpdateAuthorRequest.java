package com.example.SIDIS_Book.authormanagement.service;

import io.micrometer.common.lang.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateAuthorRequest {
    @Nullable
    private String name;
    @Nullable
    private String shortBio;
}
