package com.ncinga.chatservice.service.impl;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.admin.directory.model.UserName;
import com.google.api.services.admin.directory.model.UserPhone;
import com.google.api.services.admin.directory.model.Users;
import com.ncinga.chatservice.dto.GoogleRequestUserDto;
import com.ncinga.chatservice.dto.GoogleUserDto;
import com.ncinga.chatservice.service.GoogleOperationsService;
import com.ncinga.chatservice.service.JwtService;
import com.ncinga.chatservice.service.NewPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class GoogleOperationsServiceImpl implements GoogleOperationsService {
    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final NewPasswordService newPasswordService;
    private final String token = "";
    private static final String SERVICE_ACCOUNT_FILE = "/Users/damirugamage/Downloads/service-account.json";
    private static final String ADMIN_USER = "admin@yourdomain.com";
    private final Directory directory;


    public GoogleOperationsServiceImpl(JwtService jwtService, NewPasswordService newPasswordService, Directory directory) {
        this.jwtService = jwtService;
        this.newPasswordService = newPasswordService;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        this.directory = directory;
    }


    @Override
    public String resetUserPassword(String userEmail) {
        try {

            // Fetch the user details
            User user = directory.users().get(userEmail).execute();

            String newPassword = newPasswordService.generatePassword();

            // Update the password
            user.setPassword(newPassword);
            user.setChangePasswordAtNextLogin(true); // Force password change

            directory.users().update(userEmail, user).execute();
            return "Password reset successfully " + newPassword;
        } catch (IOException e) {
            return "Error resetting password: " + e.getMessage();
        }
    }

    @Override
    public GoogleRequestUserDto getUserInfo(String userEmail) {
        try {
            // Fetch the user details
            User user = directory.users().get(userEmail).execute();

            return new GoogleRequestUserDto(
                    user.getId(),
                    user.getPrimaryEmail(),
                    user.getName().getFullName(),
                    user.getPhones(),
                    user.getSuspended() ? "Suspended" : "Active"
            );

        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String enableUser(String userEmail) {
        try{
            User user = directory.users().get(userEmail).execute();

            user.setSuspended(false);
            directory.users().update(userEmail, user).execute();

            return "User enabled";
        } catch (IOException e) {
            return "Error enabling user: " + e.getMessage();
        }
    }

    @Override
    public String disableUser(String userEmail) {
        try{
            User user = directory.users().get(userEmail).execute();

            user.setSuspended(true);
            directory.users().update(userEmail, user).execute();

            return "User disabled";
        } catch (IOException e) {
            return "Error disabling user: " + e.getMessage();
        }
    }

    @Override
    public String deleteUser(String userEmail) {
        try{
            directory.users().delete(userEmail).execute();

            return "User deleted successfully!";

        } catch (IOException e) {

            return "Error deleting user: " + e.getMessage();
        }
    }

    @Override
    public String createUser(String firstName, String lastName, String email, String password) {
        try{
            User user = new User();
            user.setName(new UserName().setGivenName(firstName).setFamilyName(lastName));
            user.setPrimaryEmail(email);
            user.setPassword(password);

            // Insert the new user into Google Directory
            directory.users().insert(user).execute();
            return "User created successfully!";

        } catch(IOException e) {
            return "Error creating user: " + e.getMessage();
        }
    }

}




