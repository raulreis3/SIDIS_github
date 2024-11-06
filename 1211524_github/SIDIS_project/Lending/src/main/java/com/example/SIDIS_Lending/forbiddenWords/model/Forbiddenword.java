package com.example.SIDIS_Lending.forbiddenWords.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ForbiddenWords")
public class Forbiddenword {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, updatable = true)
    @NotNull
    @NotBlank
    @Setter
    @Getter
    @Size(min = 1, max = 255)
    private String word;

    protected Forbiddenword(){}

    public Forbiddenword(String word) {
        this.word = word;
    }
}
