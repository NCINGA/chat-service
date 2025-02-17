package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.Repo.UserRepo;
import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.MongoModel.User;
//import com.ncinga.chatservice.service.AzureADService;
import com.ncinga.chatservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.naming.factory.SendMailFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Controller
public class GraphqlController {

    private final ChatSinkManager<Message> chatSinkManager;
//    private final AzureADService azureADService;
    private final UserRepo userRepo;
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @QueryMapping(name = "ping")
    public String ping() {
        return "Pong";
    }

    @SubscriptionMapping(name = "messaging")
    public Flux<Message> messaging(@Argument Message message) {
        return chatSinkManager.createChatFlow(message.getUser()).asFlux();
    }

//    @MutationMapping(name = "createAzureUser")
//    public String createAzureUser(
//            @Argument String displayName,
//            @Argument String mailNickname,
//            @Argument String userPrincipalName,
//            @Argument String password
//    ) {
//        return azureADService.createUser(displayName, mailNickname, userPrincipalName, password);
//    }
//
//    // MongoDB Queries and Mutations

    @QueryMapping(name = "getUsers")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @MutationMapping(name = "addUser")
    public String addUser(@Argument String name, @Argument String email, @Argument String password, @Argument String userRole, @Argument String company) {
        // Use Lombok builder pattern to create a user
        User newUser = User.builder()
                .name(name)
                .email(email)
                .password(this.passwordEncoder.encode(password))
                .userRole(userRole)
                .company(company)
                .build();
        userService.save(newUser);

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

    @MutationMapping(name = "deleteUser")
    public String deleteUser(@Argument String id) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return "User deleted successfully.";
        } else {
            throw new RuntimeException("User not found");
        }
    }

//    @MutationMapping(name = "userLogin")
//    public String userLogin(@Argument String email, @Argument String password) {
//        Optional<User> userOptional = userRepository.findByEmail(email);
//
//        if (userOptional.isEmpty()) {
//            return "User not found";
//        }
//
//        User user = userOptional.get();
//
//        // Securely compare the hashed password
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            return "Invalid password";
//        }
//
//        return "Login successful!";
//    }


}
