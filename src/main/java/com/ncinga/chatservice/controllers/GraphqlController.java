package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Controller
public class GraphqlController {

    private final ChatSinkManager<Message> chatSinkManager;
    private final ChatService chatService;

    @QueryMapping(name = "ping")
    public String ping() {
        return "Pong";
    }

    @SubscriptionMapping(name = "subscription")
    public Flux<Message> subscription(@Argument Message message) {
        return chatSinkManager.createChatFlow(message.getUser()).asFlux();
    }

    @MutationMapping(name = "sendMessage")
    public Mono<Message> sendMessage(@Argument Message message) throws IllegalAccessException, InterruptedException {
        chatService.sendMessage(message);
        return Mono.just(message);
    }


}
