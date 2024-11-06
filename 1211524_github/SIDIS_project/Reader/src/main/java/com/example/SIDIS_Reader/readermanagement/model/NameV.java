package com.example.SIDIS_Reader.readermanagement.model;

import java.util.regex.Pattern;

public class NameV {
    private Pattern textPattern;
    private final int LENGTH = 150;
    private boolean specialCharacterVerification(final String name){
        textPattern = Pattern.compile("[^a-zA-Z0-9 ]");
        return !textPattern.matcher(name).find();
    }

    public boolean sizeVerification(final String name){

        return name.length() <= LENGTH;
    }

    public boolean validate(final String name){
        boolean flagSpecialCharacters = specialCharacterVerification(name);
        boolean flagSizeVerification = sizeVerification(name);

        return flagSpecialCharacters && flagSizeVerification;
    }


}
