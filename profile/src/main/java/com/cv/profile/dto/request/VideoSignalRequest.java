package com.cv.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoSignalRequest {
    private String type; // "offer", "answer", "candidate"
    private Object data; // Dữ liệu SDP hoặc ICE Candidate (JSON object)
    private String from;
    private String roomId;
}
