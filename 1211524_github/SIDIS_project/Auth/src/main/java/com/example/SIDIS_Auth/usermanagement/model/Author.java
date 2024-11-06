package com.example.SIDIS_Auth.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Author {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Size(max = 150)
    @NotBlank(message = "Name cannot be blank")
    @NotNull
    private String name;

    @Getter
    @Setter
    @Size(max = 4096)
    @NotBlank(message = "Short bio cannot be blank")
    @NotNull
    private String shortBio;

    @JsonIgnore
    @Getter
    @Setter
    private String photoPath;

    @Getter
    @Setter
    private String photoURL;


}
