package com.ncinga.chatservice.service;

public interface UserOffBoardingService {
    String deleteUser(String userId);
    String disableUser(String userId);
}