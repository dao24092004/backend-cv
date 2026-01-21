package com.cv.profile.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillDTO {
    private Long id;
    private String name;
    private String category;
    private Integer proficiency;
}