package com.example.SIDIS_Auth.usermanagement.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static java.time.temporal.ChronoUnit.YEARS;

@Entity
@Table(name = "Reader")
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    @NotNull
    @NotBlank
    @Setter
    @Getter
    @Size(min = 1, max = 32)
    private String readerNumber;

    //email
    @Column(nullable = false, unique = true, updatable = true)
    @NotNull
    @NotBlank
    @Getter
    @Size(min = 1, max = 255)
    private String username;


    //name (user's name)
    @Column(nullable = false, unique = false, updatable = true)
    @NotNull
    @NotBlank
    @Setter
    @Getter
    @Size(min = 1, max = 150)
    private String name;

    //dateOfBirth
    @Column(nullable = false, unique = false, updatable = true)
    @NotNull
    @NotBlank
    @Setter
    @Getter
    @Temporal(TemporalType.DATE)
    private String dateOfBirth;

    @Column(nullable = false, unique = false, updatable = true)
    @NotNull
    @NotBlank
    @Setter
    @Getter
    @Size(min = 9, max = 9)
    private String phoneNumber;

    @Column(nullable = true, unique = false, updatable = true)
    @NotNull
    @NotBlank
    @Setter
    @Getter
    private String gdprConsent;

    @Setter
    @Getter
    private boolean enabled = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    @ElementCollection
    private Set<String> interestList = new HashSet<>();

    @Getter
    @Setter
    private String fileDownload;

    protected Reader (){}

    public Reader (final String username, final String name, final String dateOfBirth, final String phoneNumber, final String gdprConsent) {
        this.username = username;
        this.name = name.trim();
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.gdprConsent = gdprConsent.toLowerCase();
    }

    public static Reader newReader(final String username, final String name, final String dateOfBirth, final String phoneNumber, final String gdprConsent, final User user) {
        final var r = new Reader(username, name,dateOfBirth, phoneNumber,gdprConsent);
        r.setUser(user);
        user.setFullName(name);
        user.addAuthority(new Role(Role.READER));
        return r;
    }

    public Long getAge(){
        String dateFormat = "dd/MM/yyyy";
        String dateFormat2 = "d/M/yyyy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(dateFormat2);

        LocalDate todayUS = LocalDate.now();
        String todayS = todayUS.getDayOfMonth()+"/"+todayUS.getMonthValue()+"/"+todayUS.getYear();

        LocalDate date = LocalDate.parse(dateOfBirth, formatter);
        LocalDate today = LocalDate.parse(todayS, formatter2);


        return YEARS.between(date,today);
    }

}
