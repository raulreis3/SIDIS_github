package com.example.SIDIS_Book.authormanagement.api;

import lombok.Getter;
import lombok.Setter;

public class TopReaderPerGenreDTO {
    @Getter
    @Setter
    private Long lendingsCount;

    // getters and setters

    public Long getLendingsCount() {
        return lendingsCount;
    }
    public void setLendingsCount(Long lendingsCount) {
        this.lendingsCount = lendingsCount;
    }
}
