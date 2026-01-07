package com.cv.profile.dto.request;

import lombok.Data;

@Data
public class CallRequest {
    private String roomId;
    private String visitorName; // Có thể là "Visitor", "Guest123", hoặc tên thật
    private Long timestamp = System.currentTimeMillis();
}
