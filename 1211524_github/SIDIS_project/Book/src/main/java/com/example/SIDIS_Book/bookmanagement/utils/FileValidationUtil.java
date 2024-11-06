package com.example.SIDIS_Book.bookmanagement.utils;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public class FileValidationUtil {

    public static void validateCoverFile(MultipartFile coverFile) {
        if (coverFile != null && !coverFile.isEmpty()) {
            if (!coverFile.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) &&
                    !coverFile.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
                throw new IllegalArgumentException("The image must be in JPEG or PNG format");
            }
            if (coverFile.getSize() > 20 * 1024) {
                throw new IllegalArgumentException("File size cannot exceed 20 KB");
            }
        }
    }
}
