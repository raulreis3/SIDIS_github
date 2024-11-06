package com.example.SIDIS_Reader.readermanagement.services;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class CreateReaderRequest {
    @NonNull
    @NotBlank
    @Email
    private String username;

    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @NotBlank
    private String dateOfBirth;

    @NonNull
    @NotBlank
    private String phoneNumber;

    @NonNull
    @NotBlank
    private String gdprConsent;

    @NonNull
    @NotBlank
    private String password;

    @NonNull
    @NotBlank
    private String rePassword;

    private Set<String> interestList = new HashSet<>();

    private Set<String> authorities = new HashSet<>();

    public CreateReaderRequest(final String username, final String name, final String dateOfBirth, final String phoneNumber, final String gdprConsent, final String password){
        this.username = username;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.gdprConsent = gdprConsent;
        this.password = password;
        this.rePassword = password;
    }

}
