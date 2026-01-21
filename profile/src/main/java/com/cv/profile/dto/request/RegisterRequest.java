package com.cv.profile.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private Long profileId;
    private String username;
    private String password;
    private String fullName;

}
