package com.cv.profile.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EventRequest {
    private String name;
    private String role;
    private LocalDate date;

    private String description;
    private String imageUrl;
}
