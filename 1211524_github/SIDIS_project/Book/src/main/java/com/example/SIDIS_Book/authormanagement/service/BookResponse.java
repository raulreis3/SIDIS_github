package com.example.SIDIS_Book.authormanagement.service;

import lombok.Getter;
import lombok.Setter;

public class BookResponse {
        @Getter
        @Setter
        private String isbn;
        @Getter
        @Setter
        private String title;
        @Getter
        @Setter
        private String genre;
        @Getter
        @Setter
        private String description;
        @Getter
        @Setter
        private String coverUrl;
}
