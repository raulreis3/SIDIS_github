package com.example.SIDIS_Lending.lendingmanagement.model;

import com.example.SIDIS_Reader.readermanagement.repositories.ReaderRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class ReaderNumber {

    private String getCurrentYear(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        LocalDateTime currentYear = LocalDateTime.now();

        return formatter.format(currentYear);
    }

    public Long getNextReaderNumber(ReaderRepository repo){
        String lastReaderNumber = repo.getLastReaderNumber(getCurrentYear());
        Pattern textPattern = Pattern.compile("/");

        return Long.parseLong(textPattern.split(lastReaderNumber)[1])+1L;
    }
    public String generate(final Long num)
    {
        String year = getCurrentYear();

        return year+"/"+num;
    }

}
