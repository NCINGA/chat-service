package com.ncinga.chatservice.service;

public interface UserOnBoardingService {
    String createUser(String displayName, String mailNickname, String userPrincipalName, String password, String mobilePhone);
}
