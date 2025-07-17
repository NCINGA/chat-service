package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.dto.FaqRequest;
import com.ncinga.chatservice.dto.FaqResponse;
import com.ncinga.chatservice.service.FaqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaqServiceImpl implements FaqService {

    private final RestTemplate restTemplate;

    @Value("${faq.api.url}")
    private String faqApiUrl;

    @Value("${faq.api.token}")
    private String authToken;

    @Override
    public FaqResponse queryFaq(FaqRequest request) {

        try {
            log.info("Sending FAQ query request: {}", request);

            HttpHeaders headers = createHeaders();
            HttpEntity<FaqRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<FaqResponse> response = restTemplate.exchange(
                    faqApiUrl,
                    HttpMethod.POST,
                    entity,
                    FaqResponse.class
            );

            FaqResponse faqResponse = response.getBody();

            log.info("FAQ API call successful", faqResponse);

            return faqResponse;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("HTTP error calling FAQ API ({}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("FAQ API error: " + e.getMessage(), e);

        } catch (Exception e) {
            log.error("Unexpected error calling FAQ API", e.getMessage(), e);
            throw new RuntimeException("Unable to process FAQ request: " + e.getMessage(), e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");
        headers.set("Authorization", "Bearer " + authToken);
        return headers;
    }
}
