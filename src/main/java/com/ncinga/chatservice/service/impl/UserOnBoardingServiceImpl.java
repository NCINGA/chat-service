package com.ncinga.chatservice.service.impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncinga.chatservice.dto.UserRequestDto;
import com.ncinga.chatservice.service.UserOnBoardingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import com.ncinga.chatservice.service.JWTService;

@Service
public class UserOnBoardingServiceImpl implements UserOnBoardingService {
    private final JWTService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(UserOnBoardingServiceImpl.class);
    private final RestTemplate restTemplate = new RestTemplate();

    public UserOnBoardingServiceImpl(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    public String createUser(String displayName, String mailNickname, String userPrincipalName, String password) {

        String token = jwtService.generateJwtToken();
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
                    passwordProfile
            );

            // Convert the request body to JSON for logging
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(user);
            logger.info("Sending request to Azure AD: {}", requestBody);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            logger.info("Response Status Code: {}", response.getStatusCode());
            logger.info("Response Body: {}", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            logger.error("Error creating Azure AD user: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }
}
