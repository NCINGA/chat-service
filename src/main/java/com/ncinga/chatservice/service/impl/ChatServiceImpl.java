package com.ncinga.chatservice.service.impl;


import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.LLMRequest;
import com.ncinga.chatservice.dto.LLMResponse;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.ChatService;
import com.ncinga.chatservice.service.LLMService;
import com.ncinga.chatservice.service.PasswordResetService;
import com.ncinga.chatservice.service.impl.workflow.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.KB_ARTICLE_ASSISTANCE;
import static com.ncinga.chatservice.service.impl.workflow.Dictionary.TEXT;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatSinkManager<Message> chatSinkManager;
    private final CommonPool commonPool;
    private final LLMService llmService;

    private final PasswordResetService passwordResetService;
    private List<WorkFlowQuestion> questions = new ArrayList<>();
    AtomicReference<String> intent = new AtomicReference<>("");

    @Override
    public void sendMessage(Message message) throws IllegalAccessException, InterruptedException {
        log.info("Chat message received from session: {}", message.getSession());
        AtomicInteger sessionIndex = commonPool.getSessionIndex(message.getSession());
        WorkflowProcess workflowProcess = null;
        if (Dictionary.GREETS.contains(message.getMessage().toLowerCase())) {
            if (sessionIndex.get() == -1) {
                sessionIndex.set(-2);
                String randomGreeting = Dictionary.GREETING_MESSAGE.get(
                        ThreadLocalRandom.current().nextInt(Dictionary.GREETING_MESSAGE.size())
                );
                sendQuestion(message.getSession(), randomGreeting, TEXT, message.getArgs());
                log.info("Greeting sent to user.");
                return;
            } else {
                log.info("User already in conversation, ignoring duplicate 'hi'.");
                return;
            }
        }

        if (sessionIndex.get() == -2) {
            LLMResponse response = llmService.detectIntent(new LLMRequest(message.getSession(), message.getMessage()));
            log.info("Detected intent: {}", response.getIntent());
            intent.set(response.getIntent());
            IntentWorkflow intentWorkflow = IntentFactory.getIntent(response.getIntent());
            questions = intentWorkflow.getQuestions();
            if (response.getIntent().equalsIgnoreCase(KB_ARTICLE_ASSISTANCE)) {
                clearSession(message.getSession());
            }
            sessionIndex.set(0);
            WorkFlowQuestion firstQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), firstQuestion.getQuestion(), firstQuestion.getInputType(), message.getArgs());
            return;
        }
        workflowProcess = WorkflowProcessFactory.getWorkflowProcess(intent.get(), chatSinkManager, commonPool, questions, passwordResetService);
        workflowProcess.execute(sessionIndex, message);

    }

    private void clearSession(String session) {
        commonPool.removeSessionData(session);
    }

    private void sendQuestion(String session, String question, String type, Object args) {
        Message questionMessage = new Message(session, Dictionary.AI, question, new Date().getTime(), type, args);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        log.info("Sent question to {}: {}", session, question);
    }


}
