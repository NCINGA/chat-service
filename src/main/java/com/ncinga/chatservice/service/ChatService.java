package com.ncinga.chatservice.service;

import com.ncinga.chatservice.dto.Message;

public interface ChatService {
    void sendMessage(Message message) throws IllegalAccessException, InterruptedException;
}
