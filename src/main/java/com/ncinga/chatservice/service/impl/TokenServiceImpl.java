package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.dto.TokenRequest;
import com.ncinga.chatservice.dto.TokenResponse;
import com.ncinga.chatservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final RestTemplate restTemplate;

    @Value("${key.clock.url}")
    private String server;
    @Value("${key.clock.username}")
    private String username;
    @Value("${key.clock.password}")
    private String password;
    @Value("${key.clock.realm}")
    private String realm;
    @Value("${key.clock.admin}")
    private String admin;

    @Value("${key.clock.clientId}")
    private String clientId;

    private static final String GRANT_TYPE = "password";

    @Override
    public TokenResponse getAuthToken() {
        try {
            String url = server + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("client_id", clientId);
            formData.add("username", username);
            formData.add("password", password);
            formData.add("grant_type", GRANT_TYPE);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
            log.debug("Request : {}", entity.getBody());
            ResponseEntity<TokenResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, TokenResponse.class);
            log.info("Response : {}", response);
            return response.getBody();

        } catch (RestClientException e) {
            log.error("Error during REST call: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
        }
        return null;
    }

}
