package com.ncinga.chatservice.service.impl;


import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.LLMRequest;
import com.ncinga.chatservice.dto.LLMResponse;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.*;
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

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatSinkManager<Message> chatSinkManager;
    private final CommonPool commonPool;
    private final LLMService llmService;
    private final LLMService2 llmService2;
    private final SMSService smsService;
    private final PasswordResetService passwordResetService;
    private final OTPGenerateService otpGenerateService;
    private List<WorkFlowQuestion> questions = new ArrayList<>();
    private final GetUserByEmailService getUserByEmailService;
    private final UserService userService;
    AtomicReference<String> intent = new AtomicReference<>("");

    @Override
    public void sendMessage(Message message) throws IllegalAccessException, InterruptedException {
        log.info("Chat message received from session: {}", message.getSession());
        AtomicInteger sessionIndex = commonPool.getSessionIndex(message.getSession());
        WorkflowProcess workflowProcess = null;
        if (Dictionary.GREETS.contains(message.getMessage().toLowerCase())) {
           // if (sessionIndex.get() == -1) {
                sessionIndex.set(-2);
                String randomGreeting = Dictionary.GREETING_MESSAGE.get(
                        ThreadLocalRandom.current().nextInt(Dictionary.GREETING_MESSAGE.size())
                );
                sendQuestion(message.getSession(), randomGreeting, TEXT, message.getArgs());
                log.info("Greeting sent to user.");
                return;
//            } else {
//                log.info("User already in conversation, ignoring duplicate 'hi'.");
//                return;
//            }
        }

        if (sessionIndex.get() == -2) {
            LLMResponse response = llmService.detectIntent(new LLMRequest(message.getSession(), message.getMessage(), null));
            log.info("Detected intent: {}", response.getIntent());
            intent.set(response.getIntent());
            IntentWorkflow intentWorkflow = IntentFactory.getIntent(response.getIntent());
            questions = intentWorkflow.getQuestions();
            if (response.getIntent().equalsIgnoreCase(KB_ARTICLE_ASSISTANCE)) {
                clearSession(message.getSession());
            }
            sessionIndex.set(0);
            WorkFlowQuestion firstQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), firstQuestion.getQuestion(), firstQuestion.getInputType(), firstQuestion.getArgs());
            if (response.getIntent().equals(REPORT_ISSUE)) {
                sessionIndex.set(0);
                workflowProcess = WorkflowProcessFactory.getWorkflowProcess(intent.get(), chatSinkManager, commonPool, questions, passwordResetService, smsService, getUserByEmailService, otpGenerateService, userService, llmService2);
                workflowProcess.execute(sessionIndex, message);
            }
            return;
        }
        workflowProcess = WorkflowProcessFactory.getWorkflowProcess(intent.get(), chatSinkManager, commonPool, questions, passwordResetService, smsService, getUserByEmailService, otpGenerateService, userService, llmService2);
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
