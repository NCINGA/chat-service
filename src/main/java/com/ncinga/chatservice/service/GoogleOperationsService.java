package com.ncinga.chatservice.service;

import com.ncinga.chatservice.dto.GoogleRequestUserDto;
import com.ncinga.chatservice.dto.GoogleUserDto;

import java.util.List;

public interface GoogleOperationsService {

    String resetUserPassword(String userEmail);

    GoogleRequestUserDto getUserInfo(String userEmail);

    String enableUser(String userEmail);

    String disableUser(String userEmail);

    String deleteUser(String userEmail);

    String createUser(String firstName, String lastName, String email, String password);
}
