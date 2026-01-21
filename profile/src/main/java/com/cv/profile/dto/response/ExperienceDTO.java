package com.cv.profile.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExperienceDTO {
    private Long id;
    private String companyName;
    private String position;
    private String startDate;
    private String endDate;
    private String description;
    private Boolean isCurrent;
}