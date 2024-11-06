package com.example.SIDIS_Lending.lendingmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Table(name = "Genre")
public class Genre {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    @NotNull
    @NotBlank
    @Getter
    public String genreName;

    protected Genre (){}

    public Genre (final String genreName)
    {
        this.genreName = genreName;
    }
}
