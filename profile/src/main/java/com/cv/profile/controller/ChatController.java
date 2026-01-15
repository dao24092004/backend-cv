package com.cv.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cv.profile.dto.request.CallRequest;
import com.cv.profile.dto.request.ChatMessageRequest;
import com.cv.profile.dto.request.VideoSignalRequest;
import com.cv.profile.service.ChatService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // --- 1. CHAT PUBLIC (Cũ - Giữ nguyên nếu muốn) ---
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageRequest sendMessage(@Payload ChatMessageRequest chatMessage) {
        return chatService.processMessage(chatMessage);
    }

    // chat user
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessageRequest addUser(@Payload ChatMessageRequest chatMessage) {
        chatMessage.setContent("đã tham gia.");
        chatMessage.setType(ChatMessageRequest.MessageType.JOIN);
        return chatMessage;
    }

    @GetMapping("/api/v1/chat/history")
    @ResponseBody
    public ResponseEntity<List<ChatMessageRequest>> getChatHistory() {
        return ResponseEntity.ok(chatService.getRecentHistory());
    }

    // --- 2. CHAT PRIVATE (MỚI - CẦN THÊM ĐOẠN NÀY) ---

    /**
     * Khách gửi tin -> Server nhận -> Chuyển tiếp cho Admin
     */
    @MessageMapping("/chat.sendToAdmin")
    public void sendToAdmin(@Payload ChatMessageRequest message) {
        System.out.println("Guest Message: " + message.getContent() + " from " + message.getSenderId());

        // Gửi vào kênh riêng của Admin
        messagingTemplate.convertAndSend("/topic/admin/messages", message);
    }

    /**
     * Admin trả lời -> Server nhận -> Chuyển tiếp cho User cụ thể
     */
    @MessageMapping("/chat.replyToUser")
    public void replyToUser(@Payload ChatMessageRequest message) {
        System.out.println("Admin Reply to " + message.getRecipientId() + ": " + message.getContent());

        // Gửi vào kênh riêng của User đó (dựa vào recipientId)
        if (message.getRecipientId() != null) {
            messagingTemplate.convertAndSend("/topic/private/" + message.getRecipientId(), message);
        }
    }

    // --- 3. VIDEO CALL (Giữ nguyên) ---
    @MessageMapping("/video.signal")
    public void handleVideoSignal(@Payload VideoSignalRequest signal) {
        messagingTemplate.convertAndSend("/topic/video/" + signal.getRoomId(), signal);
    }

    @MessageMapping("/video.request")
    public void handleCallRequest(@Payload CallRequest request) {
        messagingTemplate.convertAndSend("/topic/call-requests", request);
    }

    @MessageMapping("/video.end")
    public void handleEndCall(@Payload CallRequest request) {
        messagingTemplate.convertAndSend("/topic/call-ended/" + request.getRoomId(), request);
    }
}