package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.dto.TokenRequest;
import com.ncinga.chatservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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
    public Object getAuthToken() {
        try {
            String url = server + "/realms/" + realm + "/protocol/openid-connect/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            TokenRequest request = new TokenRequest();
            request.setClientId(clientId);
            request.setUsername(username);
            request.setPassword(password);
            request.setGrantType(GRANT_TYPE);
            HttpEntity<Object> entity = new HttpEntity<>(request, headers);
            log.debug("Request : {}", entity.getBody());
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
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
