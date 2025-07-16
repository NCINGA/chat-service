package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FaqQuery implements IntentWorkflow {

    @Value("${faq.api.url}")
    private String faqApiUrl;

    @Value("${faq.api.token}")
    private String authToken;

    @Override
    public Message conversation(Message message) {
        try {
            log.info("Calling FAQ API with question: {}", message.getMessage());

            // Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("accept", "application/json");
            headers.set("Authorization", "Bearer " + authToken);
            headers.set("Content-Type", "application/json");

            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("q", message.getMessage());
            requestBody.put("session_id", message.getSession() != null ? message.getSession() : "default");
            requestBody.put("collection_name", "faq_collection");
            requestBody.put("k", 1);

            log.info("FAQ API request body: {}", requestBody);

            // Create HTTP entity with headers and body
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Send request to FAQ API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(
                    faqApiUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            // Process response
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            if (responseBody != null && responseBody.containsKey("answer")) {
                String answer = (String) responseBody.get("answer");
                log.info("FAQ API answer extracted: {}", answer);
                return new Message(message.getSession(), null, answer, System.currentTimeMillis(), message.getInputType());
            }

            log.warn("FAQ API response does not contain 'answer' field or response is null");
            return new Message(message.getSession(), null, "I couldn't find an answer to your question. Please try rephrasing or contact support.", System.currentTimeMillis(), message.getInputType());

        } catch (Exception e) {
            log.error("Error calling FAQ API: ", e);
            return new Message(message.getSession(), null, "Sorry, I encountered an error while processing your request: " + e.getMessage(), System.currentTimeMillis(), message.getInputType());
        }
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        // No specific questions needed for FAQ flow
        return new ArrayList<>();
    }
}
