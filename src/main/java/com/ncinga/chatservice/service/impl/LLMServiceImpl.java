package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.dto.LLMRequest;
import com.ncinga.chatservice.dto.LLMResponse;
import com.ncinga.chatservice.service.LLMService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
@Slf4j
public class LLMServiceImpl implements LLMService {
    @Value("${llm.external.url}")
    private String server;
    private final RestTemplate restTemplate;

    @Override
    public LLMResponse detectIntent(LLMRequest request) {
        try {
            String url = server + "/intent/detect/";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "TmMxbmc0SDNscDM0MTI=");
            HttpEntity<LLMRequest> entity = new HttpEntity<>(request, headers);
            log.info("Request : {}", entity.getBody());
            ResponseEntity<LLMResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, LLMResponse.class);
            log.info("Response : {}", response);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error during REST call: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return null;
        }
    }
}
