package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.document.User;
import com.ncinga.chatservice.dto.AzureUserDto;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.repository.UserRepo;
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
import com.ncinga.chatservice.dto.UserDto;

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
    private final UserRepo userRepo;

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

    @MutationMapping(name = "addUser")
    public String addUser(UserDto userDto) {
        // Use Lombok builder pattern to create a user
        User newUser = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .userRole(userDto.getUserRole())
                .company(userDto.getCompany())
                .build();
//        userRepo.save(newUser)


        this.passwordEncoder.matches(password, newUser.getPassword());

        return "sucess";// Save and return the created user
    }

    @MutationMapping(name = "updateUser")
    public String updateUser(
            @Argument String id,
            @Argument String name,
            @Argument String email,
            @Argument String password,
            @Argument String userRole,
            @Argument String company) {

        return userService.findById(id).map(user -> {
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setUserRole(userRole);
            user.setCompany(company);
            userService.save(user);  // Save the updated user to the database
            return "User updated successfully";
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @MutationMapping(name = "deleteUserMongo")
    public String deleteUserMongo(@Argument String id) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return "User deleted successfully.";
        } else {
            throw new RuntimeException("User not found");
        }
    }


}
