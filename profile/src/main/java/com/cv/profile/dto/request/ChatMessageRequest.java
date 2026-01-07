package com.cv.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private String sender; // Tên hiển thị (VD: Guest 123)
    private String senderId; // ID Định danh (QUAN TRỌNG: để biết ai là ai)
    private String recipientId; // ID người nhận (Dùng khi Admin trả lời)
    private String content;
    private MessageType type;
    private String timestamp;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}