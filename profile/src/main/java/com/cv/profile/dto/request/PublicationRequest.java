package com.cv.profile.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PublicationRequest {
    private String title;
    private String publisher;
    private LocalDate releaseDate;
    private String url;

}
