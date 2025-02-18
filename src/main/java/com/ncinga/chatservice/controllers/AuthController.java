package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.dto.LoginRequest;
import com.ncinga.chatservice.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public Boolean signin(@RequestBody LoginRequest request) {
        try {
            boolean authenticated = authService.authenticate(request.getUsername(), request.getPassword());
            return authenticated;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
