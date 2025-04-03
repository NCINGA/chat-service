package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.document.User;
import com.ncinga.chatservice.dto.AzureUserDto;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.MongoUserDto;
import com.ncinga.chatservice.exception.UserNotFoundException;
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
import com.ncinga.chatservice.document.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Controller
public class GraphqlController {

    private final ChatSinkManager<Message> chatSinkManager;
    private final ChatService chatService;
    private final UserService userService;
    private final GoogleOperationsService googleOperationsService;
    private final AzureADService azureADService;
    private final JwtService jwtService;
    private final SMSService smsService;

    @QueryMapping(name = "ping")
    public String ping() {
        return "Pong";
    }

    @QueryMapping(name = "getUserByEmail")
    public AzureUserDto getUserByEmail(@Argument String email) {
        return azureADService.getUserByEmail(email);
    }

    @QueryMapping(name = "doesUserExist")
    public boolean doesUserExist(@Argument String email) {
        return azureADService.doesUserExist(email);
    }

    @QueryMapping(name = "findByRole")
    public Object findByRole(@Argument String email, @Argument String password, @Argument String userRole) {
        return userService.findByRole(email, password, userRole);
    }

    @QueryMapping(name = "doesUserExistByName")
    public Mono<Boolean> doesUserExistByName(@Argument String username) {
        return userService.doesUserExist(username);
    }

    @QueryMapping(name = "getAllUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @QueryMapping(name = "getUserById")
    public Optional<User> getUserById(@Argument String id) {
        return userService.getUserById(id);
    }

    @QueryMapping(name = "getUserIdByEmail")
    public String getUserIdByEmail(@Argument String email) {
        return azureADService.getUserIdByEmail(email);
    }


    @QueryMapping(name = "getGoogleToken")
    public String getGoogleToken() {
        return jwtService.generateGoogleToken();
    }

    @QueryMapping(name = "getUserInfo")
    public Object getUserInfo(@Argument String userEmail) {
        return googleOperationsService.getUserInfo(userEmail);
    }

    @SubscriptionMapping(name = "subscription")
    public Flux<Message> subscription(@Argument Message message) {
        return chatSinkManager.createChatFlow(message.getSession()).asFlux();
    }

    @MutationMapping(name = "deleteUser")
    public String deleteUser(@Argument String userId) {
        return azureADService.deleteUser(userId);
    }

    @MutationMapping(name = "resetPassword")
    public String resetPassword(@Argument String userId) {
        return azureADService.resetPassword(userId);
    }

    @MutationMapping(name = "createUser")
    public String createUser(@Argument String displayName, @Argument String mailNickname, @Argument String userPrincipalName, @Argument String password, @Argument String mobilePhone) {
        return azureADService.createUser(displayName, mailNickname, userPrincipalName, password, mobilePhone);
    }

    @MutationMapping(name = "unlockUser")
    public String unlockUser(@Argument String userId) {
        return azureADService.enableUserAccount(userId);
    }

    @MutationMapping(name = "disableUser")
    public String disableUser(@Argument String userId) {
        return azureADService.disableUser(userId);
    }

    @MutationMapping(name = "sendMessage")
    public Mono<Message> sendMessage(@Argument Message message) throws IllegalAccessException, InterruptedException {
        chatService.sendMessage(message);
        return Mono.just(message);
    }

    @MutationMapping(name = "register")
    public Object register(@Argument String username, @Argument String password, @Argument String email, @Argument String role) {
        MongoUserDto newUser = new MongoUserDto(username, password, email, role);
        return userService.register(newUser);
    }

    @MutationMapping(name = "updateUser")
    public Object updateUser(@Argument String id, @Argument String username, @Argument String password, @Argument String email, @Argument String role) {
        MongoUserDto user = new MongoUserDto(username, password, email, role);
        return userService.updateUser(id, user);
    }

    @MutationMapping(name = "deleteMongoUser")
    public String deleteMongoUser(@Argument String id) throws UserNotFoundException {
        return userService.deleteMongoUser(id);
    }

    @MutationMapping(name = "resetGoogleUserPassword")
    public String resetUserPassword(@Argument String userEmail) {
        return googleOperationsService.resetUserPassword(userEmail);
    }

    @MutationMapping(name = "createGoogleUser")
    public String createGoogleUser(String firstName, String lastName, String email, String password) {
        return googleOperationsService.createUser(firstName, lastName, email, password);
    }

    @MutationMapping(name = "enableGoogleUser")
    public String enableGoogleUser(@Argument String userEmail) {
        return googleOperationsService.enableUser(userEmail);
    }

    @MutationMapping(name = "disableGoogleUser")
    public String disableGoogleUser(@Argument String userEmail) {
        return googleOperationsService.disableUser(userEmail);
    }

    @MutationMapping(name = "deleteGoogleUser")
    public String deleteGoogleUser(@Argument String userEmail) {
        return googleOperationsService.deleteUser(userEmail);
    }

    @QueryMapping(name = "generateOTP")
    public String generateOTP() {
        return smsService.generateOTP();
    }

    @MutationMapping(name = "send")
    public String send(@Argument String number) {
        return smsService.send(number);
    }
}
