package com.example.SIDIS_Reader.readermanagement.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginationRequest {
    @Valid
    @NotNull
    Page page;
}
