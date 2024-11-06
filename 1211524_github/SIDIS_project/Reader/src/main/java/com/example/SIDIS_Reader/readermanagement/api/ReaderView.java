package com.example.SIDIS_Reader.readermanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReaderView {
    private String readerNumber;
    private String username;
    private String name;
    private Long age;
    private String phoneNumber;
    private String gdprConsent;
    private Set<String> interestList;
    private String fileDownload;
}
