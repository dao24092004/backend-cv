package com.cv.profile.dto.request;

import lombok.Data;

@Data
public class SkillRequest {
    private String name;
    private String category;
    private Integer proficiency;
}
