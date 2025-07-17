package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.FaqRequest;
import com.ncinga.chatservice.dto.FaqResponse;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.FaqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FaqQuery implements IntentWorkflow {

    private final FaqService faqService;

    @Value("${faq.default.collection:faq_collection}")
    private String defaultCollection;

    @Value("${faq.default.k:1}")
    private int defaultK;

    @Override
    public Message conversation(Message message) {
        try {
            log.info("Processing FAQ query for session: {}, question: {}",
                    message.getSession(), message.getMessage());

            FaqRequest faqRequest = FaqRequest.builder()
                    .q(message.getMessage())
                    .sessionId(message.getSession() != null ? message.getSession() : "default")
                    .collectionName(defaultCollection)
                    .k(defaultK)
                    .build();

            FaqResponse faqResponse = faqService.queryFaq(faqRequest);

            if (faqResponse != null && faqResponse.getAnswer() != null && !faqResponse.getAnswer().trim().isEmpty()) {
                log.info("FAQ answer found for session: {}", message.getSession());
                return new Message(
                        message.getSession(),
                        null,
                        faqResponse.getAnswer(),
                        System.currentTimeMillis(),
                        message.getInputType()
                );
            }

            log.warn("No valid answer found in FAQ response for session: {}", message.getSession());
            return new Message(
                    message.getSession(),
                    null,
                    "I couldn't find a relevant answer to your question. Please try rephrasing or contact support for assistance.",
                    System.currentTimeMillis(),
                    message.getInputType()
            );

        } catch (Exception e) {
            log.error("Error processing FAQ query for session: {}, error: {}",
                    message.getSession(), e.getMessage(), e);
            return new Message(
                    message.getSession(),
                    null,
                    "I'm experiencing technical difficulties. Please try again later or contact support.",
                    System.currentTimeMillis(),
                    message.getInputType()
            );
        }
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        return new ArrayList<>();
    }
}
