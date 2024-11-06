package com.example.SIDIS_Reader.readermanagement.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SearchRequest<T> {
    @Valid
    @NotNull
    Page page;

    @Valid
    @NotNull
    T query;
}
