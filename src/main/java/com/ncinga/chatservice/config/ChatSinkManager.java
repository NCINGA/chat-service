package com.ncinga.chatservice.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
@Service
public class ChatSinkManager<T> {
    private final Map<String, Sinks.Many<T>> chatSink = new ConcurrentHashMap<>();

    public Sinks.Many<T> createChatFlow(String user) {
        return chatSink.computeIfAbsent(user, k -> Sinks.many().replay().all());
    }
}
