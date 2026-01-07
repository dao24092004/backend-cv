package com.cv.profile.service.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.cv.profile.dto.request.ChatMessageRequest;
import com.cv.profile.service.ChatService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    // Kafka producer

    // Lưu lịch sử chat trong RAM (thread-safe)
    private final List<ChatMessageRequest> chatHistory = Collections.synchronizedList(new ArrayList<>());

    private static final int MAX_HISTORY = 50;

    @Override
    public ChatMessageRequest processMessage(ChatMessageRequest message) {

        // 1. Gán thời gian server
        message.setTimestamp(
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // 2. Lưu lịch sử ngắn hạn
        synchronized (chatHistory) {
            if (chatHistory.size() >= MAX_HISTORY) {
                chatHistory.remove(0);
            }
            chatHistory.add(message);
        }

        // 3. Gửi log sang Kafka (best-effort)
        try {
            String logMsg = String.format(
                    "[%s] %s: %s",
                    message.getTimestamp(),
                    message.getSender(),
                    message.getContent());
        } catch (Exception e) {
            System.err.println(
                    "Warning: Không thể gửi tin nhắn tới Kafka (Kiểm tra Docker Kafka).");
        }

        return message;
    }

    @Override
    public List<ChatMessageRequest> getRecentHistory() {
        return new ArrayList<>(chatHistory);
    }
}
