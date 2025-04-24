package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.LLMRequest;
import com.ncinga.chatservice.dto.LLMResponse;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.service.LLMService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.AI;
import static com.ncinga.chatservice.service.impl.workflow.Dictionary.TEXT;


@RequiredArgsConstructor
public class GeneralProcess implements WorkflowProcess {
    private final ChatSinkManager<Message> chatSinkManager;
    private final LLMService llmService;

    @Override
    public void execute(AtomicInteger sessionIndex, Message message) {
        LLMRequest request = new LLMRequest(message.getSession(), message.getMessage());
        LLMResponse llmResponse = llmService.detectIntent(request);
        Message aiResponse = new Message(llmResponse.getSessionId(), AI, llmResponse.getResponseText(), new Date().getTime(), TEXT);
        chatSinkManager.getChatSink().get(llmResponse.getSessionId()).tryEmitNext(aiResponse);

    }
}
