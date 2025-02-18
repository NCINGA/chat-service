package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.service.UserService;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Argument;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ncinga.chatservice.dto.AuthenticateDto;

@RestController
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/ping")
    public String ping() {
        return "Pong";
    }

    @GetMapping(name = "/adminCheck")
    public String adminCheck(@Argument String email, @Argument String password)  {
        return userService.isAdmin(email, password);
    }
}
