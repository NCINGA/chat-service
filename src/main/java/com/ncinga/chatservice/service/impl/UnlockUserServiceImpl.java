package com.ncinga.chatservice.service.impl;
import com.ncinga.chatservice.service.JwtService;
import com.ncinga.chatservice.service.UnlockUserService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Service
public class UnlockUserServiceImpl implements UnlockUserService {

    private final RestTemplate restTemplate;
    private final JwtService jwtService;

    public UnlockUserServiceImpl(JwtService jwtService) {
        this.jwtService = jwtService;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

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
