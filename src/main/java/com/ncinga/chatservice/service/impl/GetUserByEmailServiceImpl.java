package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.dto.AzureUserDto;
import com.ncinga.chatservice.service.GetUserByEmailService;
import com.ncinga.chatservice.service.JwtService;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class GetUserByEmailServiceImpl implements GetUserByEmailService {

    private final RestTemplate restTemplate;
    private final JwtService jwtService;

    public GetUserByEmailServiceImpl(JwtService jwtService) {
        this.jwtService = jwtService;
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
}

