package com.ncinga.chatservice.service;

public interface AuthService {
    boolean authenticate(String username, String rawPassword);
}
