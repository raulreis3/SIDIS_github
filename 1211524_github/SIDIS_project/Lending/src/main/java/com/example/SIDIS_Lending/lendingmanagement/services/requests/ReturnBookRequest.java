package com.example.SIDIS_Lending.lendingmanagement.services.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ReturnBookRequest {

    @NotBlank
    @NotNull
    @Size(max = 1024)
    private String comment;

}


