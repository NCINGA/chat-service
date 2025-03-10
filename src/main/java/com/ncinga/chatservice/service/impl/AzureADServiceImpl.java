package com.ncinga.chatservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncinga.chatservice.dto.AzureUserDto;
import com.ncinga.chatservice.dto.UserRequestDto;
import com.ncinga.chatservice.service.AzureADService;
import com.ncinga.chatservice.service.JwtService;
import com.ncinga.chatservice.service.NewPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AzureADServiceImpl implements AzureADService {

    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final NewPasswordService newPasswordService;

    public AzureADServiceImpl(JwtService jwtService, NewPasswordService newPasswordService) {
        this.jwtService = jwtService;
        this.newPasswordService = newPasswordService;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Override
    public AzureUserDto getUserByEmail(String email) {
        String url = "https://graph.microsoft.com/v1.0/users/"+ email;
        String token = jwtService.generateAzureADToken();

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<AzureUserDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<AzureUserDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                AzureUserDto.class
        );

        return response.getBody();

    }

    @Override
    public Boolean doesUserExist(String email) {
        String url = "https://graph.microsoft.com/v1.0/users/"+ email;
        String token = jwtService.generateAzureADToken();

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<AzureUserDto> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<AzureUserDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    AzureUserDto.class
            );

            return response.getBody() != null;

        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUserIdByEmail(String email) {
        String url = "https://graph.microsoft.com/v1.0/users/"+ email;
        String token = jwtService.generateAzureADToken();

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<AzureUserDto> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<AzureUserDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                AzureUserDto.class
        );

        AzureUserDto azureUser = response.getBody();

        if (azureUser == null) {
            return null;
        }
        return azureUser.getId();
    }

    @Override
    public String resetPassword(String userId) {
        String url = "https://graph.microsoft.com/v1.0/users/" + userId;
        String token = jwtService.generateAzureADToken();
        String newPassword = newPasswordService.generatePassword();

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token); // Replace with a valid token

        // Body
        Map<String, Object> passwordProfile = new HashMap<>();
        passwordProfile.put("password", newPassword);
        passwordProfile.put("forceChangePasswordNextSignIn", true);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("passwordProfile", passwordProfile);

        // HTTP request
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, String.class);
            log.info("response {}", response);
            return "Success your password :" + newPassword;
        } catch (Exception e) {
            log.error("error {}", e.getMessage());
            return "Opps !!! something happened wrong!!";
        }
    }

    @Override
    public String createUser(String displayName, String mailNickname, String userPrincipalName, String password, String mobilePhone) {

        String token = jwtService.generateAzureADToken();
        String url = "https://graph.microsoft.com/v1.0/users";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            Map<String, Object> passwordProfile = new HashMap<>();
            passwordProfile.put("forceChangePasswordNextSignIn", true);
            passwordProfile.put("password", password);

            UserRequestDto user = new UserRequestDto(
                    true,
                    displayName,
                    mailNickname,
                    userPrincipalName,
                    mobilePhone,
                    passwordProfile

            );

            // Convert the request body to JSON for logging
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(user);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);


            return response.getBody();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public String deleteUser(String userId) {
        String url = "https://graph.microsoft.com/v1.0/users/" + userId;
        String token = jwtService.generateAzureADToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
            return "Response: " + response.getBody();
        } catch (Exception e) {
            return "Failed to delete user: " + e.getMessage();
        }
    }

    @Override
    public String disableUser(String userId) {
        String url = "https://graph.microsoft.com/v1.0/users/" + userId;
        String token = jwtService.generateAzureADToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"accountEnabled\": false}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, String.class);
            return "Response: " + response.getBody();
        } catch (Exception e) {
            return "Failed to enable user: " + e.getMessage();
        }
    }

    @Override
    public String enableUserAccount(String userId) {
        String url = "https://graph.microsoft.com/v1.0/users/" + userId;
        String token = jwtService.generateAzureADToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        String requestBody = "{\"accountEnabled\": true}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, String.class);
            return "Response: " + response.getBody();
        } catch (Exception e) {
            return "Failed to enable user: " + e.getMessage();
        }
    }
}
