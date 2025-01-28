package com.ncinga.chatservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping(path = "/ping")
    public String ping() {
        return "Pong";
    }
}
