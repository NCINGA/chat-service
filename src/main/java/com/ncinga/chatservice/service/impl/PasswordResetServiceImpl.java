package com.ncinga.chatservice.service.impl;
import com.ncinga.chatservice.service.JWTService;
import com.ncinga.chatservice.service.PasswordResetService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.HashMap;
import java.util.Map;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final RestTemplate restTemplate;
    private final JWTService jwtService;

    public PasswordResetServiceImpl(JWTService jwtService) {
        this.jwtService = jwtService;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    public String resetPassword(String userId, String newPassword) {
        String url = "https://graph.microsoft.com/v1.0/users/" + userId;
        String token = jwtService.generateJwtToken();

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
            return newPassword;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}