package com.cv.profile.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EducationRequest {
    private String schoolName;
    private String degree;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

}
