package com.example.SIDIS_Reader.readermanagement.model;

import lombok.Getter;

/**
 * based on https://gist.github.com/captain-proton/d7c3e2900c8ef4f974258eb3334c1e0d
 */
@Getter
public class ISBN {

    /**
     * <p>The validation property contains all properties that are validation in a isbn</p>
     * <dl>
     * <dt>Empty</dt>
     * <dd>Null or empty values</dd>
     * <dt>AllowedCharacters</dt>
     * <dd>Only numbers, hyphens and X for isbn 10 is allowed</dd>
     * <dt>DigitCount</dt>
     * <dd>10 digits for ISBN-10 and 13 digits for ISBN-13</dd>
     * <dt>HyphenCount</dt>
     * <dd>Either zero hyphens or 3 hyphens in total for ISBN-10 or 4 for ISBN-13</dd>
     * <dt>Format</dt>
     * <dd>Simple formats as number groups contain different sizes of digits</dd>
     * <dt>CheckDigit</dt>
     * <dd>Check of last digit through calculation</dd>
     * </dl>
     */
    public enum ValidationProperty {
        Empty,
        AllowedCharacters,
        DigitCount,
        HyphenCount,
        Format,
        CheckDigit
    }

    private static final String ISBN_13_HYPHENED_FORMAT = "^[0-9]+(-([0-9]+)){4}$";
    private static final String ISBN_13_UNHYPHENED_FORMAT = "^[0-9]{13}$";
    private static final String ISBN_10_HYPHENED_FORMAT = "^[0-9]+(-([0-9X]+)){3}$";
    private static final String ISBN_10_UNHYPHENED_FORMAT = "^[0-9X]{10}$";

    private static final char HYPHEN = '-';
    private static final char X = 'X';
    private static final String X_STR = Character.toString(X);

    private final String value;

    /**
     * Contains the property after a validation process was aborted.
     * -- GETTER --
     *  Returns the validation property that was set if validation failed.
     *
     * @return one of {@linkplain ValidationProperty} values

     */
    private ValidationProperty invalidProperty;
    private int hyphens;
    private int digits;

    /**
     * Constructs a new ISBN with target value.
     *
     * @param value isbn to validate
     */
    public ISBN(String value) {
        this.value = value;
    }

    /**
     * <p>Validates the isbn given in this object through the different validation processes. If one validation
     * failed the process is aborted and the {@linkplain #getInvalidProperty()} can be retrieved.</p>
     */
    public void validate() {

        try {
            /*
             this has to be done in order! otherwise it is not guaranteed that values, for example digit and
             hyphen count, are set.
            */
            validateEmptyValue();
            validateAllowedCharacters();
            validateDigits();
            validateHyphens();

            switch (getDigits()) {
                case 10:
                    validateIsbn10Format();
                    checkDigitIsbn10();
                    break;
                case 13:
                    validateIsbn13Format();
                    checkDigitIsbn13();
                    break;
            }
        } catch (IllegalISBNFormatException e) {
            this.invalidProperty = e.property;
        }
    }

    public boolean isValid() {
        return invalidProperty == null;
    }

    private void validateIsbn13Format() throws IllegalISBNFormatException {
        String format = hyphens == 4
                ? ISBN_13_HYPHENED_FORMAT
                : ISBN_13_UNHYPHENED_FORMAT;
        if (!value.matches(format))
            throw new IllegalISBNFormatException(ValidationProperty.Format);
    }

    private void checkDigitIsbn13() throws IllegalISBNFormatException {

        String digits = value.replaceAll(Character.toString(HYPHEN), "");
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int number = Integer.valueOf(digits.substring(i, i + 1));
            sum += number * Math.pow(3, i % 2);
        }
        int checkDigit = Integer.valueOf(digits.substring(12));
        int calculatedCheckDigit = (10 - sum % 10) % 10;
        if (checkDigit != calculatedCheckDigit)
            throw new IllegalISBNFormatException(ValidationProperty.CheckDigit);
    }

    private void validateIsbn10Format() throws IllegalISBNFormatException {
        String format = hyphens == 3
                ? ISBN_10_HYPHENED_FORMAT
                : ISBN_10_UNHYPHENED_FORMAT;
        if (!value.matches(format))
            throw new IllegalISBNFormatException(ValidationProperty.Format);
    }

    private void checkDigitIsbn10() throws IllegalISBNFormatException {
        String digits = value.replaceAll(Character.toString(HYPHEN), "");
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int multiplier = 10 - i;
            String character = digits.substring(i, i + 1);
            int number = i != 9 && !character.equals(X_STR)
                    ? Integer.valueOf(character)
                    : 10;
            sum += number * multiplier;
        }
        String checkDigitStr = digits.substring(9);
        int checkDigit = !checkDigitStr.equals(X_STR)
                ? Integer.valueOf(checkDigitStr)
                : 10;
        int calculatedCheckDigit = (11 - sum % 11) % 11;
        if (checkDigit != calculatedCheckDigit)
            throw new IllegalISBNFormatException(ValidationProperty.CheckDigit);
    }

    private void validateHyphens() throws IllegalISBNFormatException {

        /*
        if ten digits are given zero or three hyphens are allowed.
        if thirteen digits are given zero or four hyphens are allowed.
         */
        boolean isValid = (digits == 10 && (hyphens == 0 || hyphens == 3))
                || (digits == 13 && (hyphens == 0 || hyphens == 4));
        if (!isValid) {
            throw new IllegalISBNFormatException(ValidationProperty.HyphenCount);
        }
    }

    private void validateDigits() throws IllegalISBNFormatException {

        boolean isValid = digits == 10 || digits == 13;
        if (!isValid) {
            throw new IllegalISBNFormatException(ValidationProperty.DigitCount);
        }
    }

    private void validateAllowedCharacters() throws IllegalISBNFormatException {

        // allowed characters are [0-9], - and X
        hyphens = 0;
        digits = 0;
        boolean isValid = true;
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            isValid = isValid
                    && (Character.isDigit(character) || HYPHEN == character || X == character);
            hyphens += HYPHEN == character
                    ? 1
                    : 0;
            digits += Character.isDigit(character) || X == character
                    ? 1
                    : 0;
        }
        if (!isValid)
            throw new IllegalISBNFormatException(ValidationProperty.AllowedCharacters);
    }

    private void validateEmptyValue() throws IllegalISBNFormatException {

        // empty values are not allowed
        boolean isValid = value != null && !value.isEmpty();
        if (!isValid) {
            throw new IllegalISBNFormatException(ValidationProperty.Empty);
        }
    }

    private static class IllegalISBNFormatException extends Exception {

        private final ValidationProperty property;

        private IllegalISBNFormatException(ValidationProperty property) {
            this.property = property;
        }
    }

}
