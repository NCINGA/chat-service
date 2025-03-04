package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.dto.AuthenticateDto;
import com.ncinga.chatservice.dto.SuccessAuthenticateDto;
import com.ncinga.chatservice.service.JwtService;
import com.ncinga.chatservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticateController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public SuccessAuthenticateDto register(@RequestBody AuthenticateDto authenticateDto) throws Exception {
        log.info("{}", authenticateDto);
        SuccessAuthenticateDto response = this.userService.login(authenticateDto);
        return response;
    }

//    @PostMapping("/register")
//    public User register(@RequestBody User user) {
//        return userService.register(user);
//    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            userService.logout(token);
            return ResponseEntity.ok("Logout successful");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Logout failed: " + e.getMessage());
        }
    }

}
