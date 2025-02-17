package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.AzureUserDto;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.service.*;
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

import java.util.List;


@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Controller
public class GraphqlController {

    private final ChatSinkManager<Message> chatSinkManager;
    private final UserOffBoardingService userOffBoardingService;
    private final PasswordResetService passwordResetService;
    private final UserOnBoardingService userOnBoardingService;
    private final UnlockUserService unlockUserService;
    private final ChatService chatService;
    private final GetUserByEmailService getUserByEmailService;

    @QueryMapping(name = "ping")
    public String ping() {
        return "Pong";
    }

    @QueryMapping(name = "getUserByEmail")
    public AzureUserDto getUserByEmail(@Argument String email) {
        return getUserByEmailService.getUserByEmail(email);
    }

    @QueryMapping(name = "doesUserExist")
    public boolean doesUserExist(@Argument String email) {
        return getUserByEmailService.doesUserExist(email);
    }

    @SubscriptionMapping(name = "subscription")
    public Flux<Message> subscription(@Argument Message message) {
        return chatSinkManager.createChatFlow(message.getSession()).asFlux();
    }

    @MutationMapping(name = "deleteUser")
    public String deleteUser(@Argument String userId) {
        return userOffBoardingService.deleteUser(userId);
    }

    @MutationMapping(name = "resetPassword")
    public String resetPassword(@Argument String userId) {
        return passwordResetService.resetPassword(userId);
    }

    @MutationMapping(name = "createUser")
    public String createUser(@Argument String displayName, @Argument String mailNickname, @Argument String userPrincipalName, @Argument String password, @Argument String mobilePhone) {
        return userOnBoardingService.createUser(displayName, mailNickname, userPrincipalName, password, mobilePhone);
    }

    @MutationMapping(name = "unlockUser")
    public String unlockUser(@Argument String userId) {
        return unlockUserService.enableUserAccount(userId);
    }

    @MutationMapping(name = "disableUser")
    public String disableUser(@Argument String userId) {
        return userOffBoardingService.disableUser(userId);
    }

    @MutationMapping(name = "sendMessage")
    public Mono<Message> sendMessage(@Argument Message message) throws IllegalAccessException, InterruptedException {
        chatService.sendMessage(message);
        return Mono.just(message);
    }


}
