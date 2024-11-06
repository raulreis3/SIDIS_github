package com.example.SIDIS_Reader.readermanagement.services;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReaderRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String dateOfBirth;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String gdprConsent;

    @NotBlank
    private String password;

    private Set<String> interestList = new HashSet<>();
}
