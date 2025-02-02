package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.dto.Chat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;

import java.time.Duration;


@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Controller
public class GraphqlController {


    @QueryMapping(name = "ping")
    public String ping() {
        return "Pong";
    }

    @SubscriptionMapping(name = "chat")
    public Flux<Chat> chat(@Argument Chat chat) {
        return Flux.interval(Duration.ofSeconds(5)).flatMap(sequence -> {
            return Flux.just(new Chat("AI", "Hi "+ chat.getUser() + " Test subscription "));
        });
    }

}
