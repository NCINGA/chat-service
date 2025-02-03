package com.ncinga.chatservice.service.impl;


import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatSinkManager<Message> chatSinkManager;


    @Override
    public void send() {
        log.info("Chat message sending");
        Sinks.EmitResult chatSinkResult = chatSinkManager.getChatSink().get("user1")
                .tryEmitNext(new Message("Shehan", "Hi..", new Date().getTime(), "none"));
        if (chatSinkResult.isSuccess()) {
            log.info("Send data");
        } else if (chatSinkResult.isFailure()) {
            log.error("cannot send data");
        }
    }
}
