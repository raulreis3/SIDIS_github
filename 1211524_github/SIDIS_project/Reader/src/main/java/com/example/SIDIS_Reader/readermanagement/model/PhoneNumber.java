package com.example.SIDIS_Reader.readermanagement.model;

import java.util.regex.Pattern;

public class PhoneNumber {
    static final int LENGTH = 9;

    private boolean charsVerification(final String phoneNumber){
        //Cant contain anything other than numbers
        Pattern textPattern;
        textPattern = Pattern.compile("[^0-9]");
        return !textPattern.matcher(phoneNumber).find();
    }

    private boolean sizeVerification(final String phoneNumber){
        return phoneNumber.length() == LENGTH;
    }

    public boolean validate(final String phoneNumber){
        boolean flagNotNumbers = charsVerification(phoneNumber);
        boolean flagSizeVerification = sizeVerification(phoneNumber);

        return flagNotNumbers && flagSizeVerification;
    }

}
