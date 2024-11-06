package com.example.SIDIS_Reader.readermanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ReaderPfp {
    @Id
    @GeneratedValue
    @Getter
    private Long pk;

    @OneToOne
    private Reader reader;

    @Setter
    @Getter
    private String downloadUri;

    // in this case we are storing the image in the database, but it would be
    // "better" to store it in a server file system
    @Lob
    @Getter
    @Setter
    private byte[] image;

    @Getter
    private String contentType;

    protected ReaderPfp() {

    }

    public ReaderPfp(final Reader reader, final byte[] image, final String contentType, final String name, final String downloadUri){
        this.reader = reader;
        this.image = image;
        this.contentType = contentType;
        this.downloadUri = downloadUri;
    }
}
