package com.example.SIDIS_Lending.lendingmanagement.services.requests;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class CreateLendingRequest {

    @NonNull
    @NotBlank
    private String isbn;

    @NonNull
    @NotBlank
    private String readerNumber;

}
