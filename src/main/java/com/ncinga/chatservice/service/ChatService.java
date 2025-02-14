package com.ncinga.chatservice.service;

import com.ncinga.chatservice.dto.Message;

public interface ChatService {
    public void sendMessage(Message message) throws IllegalAccessException, InterruptedException;
}
