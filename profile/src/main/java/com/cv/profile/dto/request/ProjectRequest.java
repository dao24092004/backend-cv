package com.cv.profile.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class ProjectRequest {
    private String title;
    private String role;
    private String customer;
    private String description;
    private List<String> technologies;
    private String imageUrl;
    private List<String> gallery; // Thêm trường này để hỗ trợ nhiều ảnh
    private String repoUrl;
    private Long profileId;
}
