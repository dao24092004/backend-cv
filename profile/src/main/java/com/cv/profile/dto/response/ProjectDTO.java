package com.cv.profile.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ProjectDTO {
    private Long id;
    private String name;
    private String role;
    private String description;
    private String imageUrl;
    private String sourceCodeUrl;
    private List<String> technologies;
}