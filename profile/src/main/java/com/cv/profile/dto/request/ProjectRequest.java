package com.cv.profile.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class ProjectRequest {
    private String name;
    private String role;
    private String customer;
    private String description;
    private String techStack; // String: "Java, React"
    private String imageUrl;
    private List<String> gallery; // Thêm trường này để hỗ trợ nhiều ảnh
    private String sourceCodeUrl;
}
