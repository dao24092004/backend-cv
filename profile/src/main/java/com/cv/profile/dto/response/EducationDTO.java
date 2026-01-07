package com.cv.profile.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EducationDTO {
    private String school;
    private String degree;
    private String period;
    private String description;
}