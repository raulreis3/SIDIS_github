package com.example.SIDIS_Book.authormanagement.service;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

    @Data
    @RequiredArgsConstructor
    @NoArgsConstructor
    public class CreateAuthorRequest {
        @NonNull
        @NotBlank
        private String name;

        @NonNull
        @NotBlank
        private String shortBio;


}
