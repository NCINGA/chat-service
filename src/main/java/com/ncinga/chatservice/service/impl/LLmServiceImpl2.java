package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.dto.LLMRequest;
import com.ncinga.chatservice.dto.LLMResponse;
import com.ncinga.chatservice.service.LLMService2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLmServiceImpl2 implements LLMService2 {
    @Value("${llm.external.url2}")
    private String server;
    private final RestTemplate restTemplate;

    @Override
    public LLMResponse createIssueChain(LLMRequest request) {
        try {
            Map<String, Object> llmRequest = new HashMap<>();
            llmRequest.put("email", request.getEmail());
            llmRequest.put("user_query", request.getQuery());
            llmRequest.put("session_id", request.getSessionId());
            String url = server + "/api/create_ticket";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.set("Authorization", "TmMxbmc0SDNscDM0MTI=");
            HttpEntity<Object> entity = new HttpEntity<>(request, headers);
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
