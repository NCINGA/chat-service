package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.document.User;
import com.ncinga.chatservice.dto.AuthenticateDto;
import com.ncinga.chatservice.dto.SuccessAuthenticateDto;
import com.ncinga.chatservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticateController {

    private final UserService userService;

    @PostMapping("/login")
    public SuccessAuthenticateDto register(@RequestBody AuthenticateDto authenticateDto) throws Exception {
        log.info("{}", authenticateDto);
        SuccessAuthenticateDto response = this.userService.login(authenticateDto);
        return response;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

}
