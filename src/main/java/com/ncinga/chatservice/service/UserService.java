package com.ncinga.chatservice.service;

import com.ncinga.chatservice.document.User;
import com.ncinga.chatservice.dto.AuthenticateDto;
import com.ncinga.chatservice.dto.MongoUserDto;
import com.ncinga.chatservice.dto.SuccessAuthenticateDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    SuccessAuthenticateDto login(AuthenticateDto authenticateDto) throws Exception;

    Mono<Map<String, Object>> register(MongoUserDto user);

    Object findByRole(String email,String password,String userRole);

    Mono<Map<String, Object>> updateUser(String id, MongoUserDto user);

    String deleteMongoUser(String id);

    Mono<Boolean> doesUserExist(String username);

    List<User> getAllUsers();

    Optional<User> getUserById(String id);

    void logout(String token) throws Exception;

}
