package com.cv.profile.service;

import java.util.List;

import com.cv.profile.dto.request.ChatMessageRequest;

public interface ChatService {

    ChatMessageRequest processMessage(ChatMessageRequest message);

    List<ChatMessageRequest> getRecentHistory();
}
