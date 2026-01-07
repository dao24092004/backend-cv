package com.cv.profile.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactDTO {
    private String email;
    private String phone;
    private String address;
    private String linkedin;
    private String github;
}
