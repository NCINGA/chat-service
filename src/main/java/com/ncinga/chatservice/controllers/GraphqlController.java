package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.service.UnlockUserService;
import com.ncinga.chatservice.service.UserOffBoardingService;
import com.ncinga.chatservice.service.PasswordResetService;
import com.ncinga.chatservice.service.UserOnBoardingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;


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

    @QueryMapping(name = "ping")
    public String ping() {
        return "Pong";
    }

    @SubscriptionMapping(name = "messaging")
    public Flux<Message> messaging(@Argument Message message) {
        return chatSinkManager.createChatFlow(message.getUser()).asFlux();
    }

    @MutationMapping(name = "deleteUser")
    public String deleteUser(@Argument String userId) {
        return userOffBoardingService.deleteUser(userId);
    }

    @MutationMapping(name = "resetPassword")
    public String resetPassword(@Argument String userId, @Argument String newPassword) {
        return passwordResetService.resetPassword(userId, newPassword);
    }

    @MutationMapping(name = "createUser")
    public String createUser(@Argument String displayName, @Argument String mailNickname, @Argument String userPrincipalName, @Argument String password) {
        return userOnBoardingService.createUser(displayName, mailNickname, userPrincipalName, password);
    }

    @MutationMapping(name = "unlockUser")
    public String unlockUser(@Argument String userId) {
        return unlockUserService.enableUserAccount(userId);
    }

    @MutationMapping(name = "disableUser")
    public String disableUser(@Argument String userId) {
        return userOffBoardingService.disableUser(userId);
    }

}
