package com.ncinga.chatservice.service;

import com.ncinga.chatservice.dto.AzureUserDto;

public interface AzureADService {

    AzureUserDto getUserByEmail(String email);

    Boolean doesUserExist(String email);

    String getUserIdByEmail(String email);

    String resetPassword(String userId);

    String createUser(String displayName, String mailNickname, String userPrincipalName, String password, String mobilePhone);

    String deleteUser(String userId);

    String disableUser(String userId);

    String enableUserAccount(String userId);

}
