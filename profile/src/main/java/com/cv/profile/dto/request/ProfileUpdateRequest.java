package com.cv.profile.dto.request;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private Long id;
    private String fullName;
    private String jobTitle;
    private String email;
    private String phone;
    private String address;
    private String bio;
    private String avatarUrl;
    private String linkedin;
    private String github;
    private Long departmentId;
}