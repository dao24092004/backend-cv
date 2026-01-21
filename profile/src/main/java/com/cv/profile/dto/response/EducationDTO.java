package com.cv.profile.dto.response;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EducationDTO {
    private Long id;
    private String school;
    private String degree;
    private String period;
    private LocalDate startDate;
    private String description;
}