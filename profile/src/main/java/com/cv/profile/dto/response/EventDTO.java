package com.cv.profile.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDTO {
    private Long id;
    private String name;
    private String role;
    private String date;
    private String description;
    private String imageUrl;
}
