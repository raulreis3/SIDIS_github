package com.example.SIDIS_Book.authormanagement.api;

import lombok.Getter;
import lombok.Setter;

public class PhotoResponse {
    @Getter
    @Setter
    private String photoURL;
    @Getter
    @Setter
    private String photoPath;

    public PhotoResponse(String photoURL, String photoPath) {
        this.photoURL = photoURL;
        this.photoPath = photoPath;
    }
}
