package com.cv.profile.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ExperienceRequest {
    private String companyName;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String description;
}
