package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.*;
import com.ncinga.chatservice.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.TEXT;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateTicketWorkflow implements WorkflowProcess {
    private final ChatSinkManager<Message> chatSinkManager;
    private final CommonPool commonPool;
    private final List<WorkFlowQuestion> questions;
    private final PasswordResetService passwordResetService;
    private final GetUserByEmailService getUserByEmailService;
    private final SMSService smsService;
    private final OTPGenerateService otpGenerateService;
    private final UserService userService;
    private final LLMService2 llmService2;

    @Override
    public void execute(AtomicInteger sessionIndex, Message message) throws IllegalAccessException, InterruptedException {
        int index = sessionIndex.get();
        WorkFlowQuestion nextQuestion;

        if (index == -1) {
            commonPool.getUserResponses().putIfAbsent(message.getSession(), new HashMap<>());
            commonPool.removeSessionData(message.getSession());
            sessionIndex.set(0);
            log.info("New session started for user: {}", message.getSession());
        }

        if (index + 1 < questions.size()) {
            sessionIndex.incrementAndGet();
        }

        log.info("message : {}", message);
        commonPool.getUserResponses().putIfAbsent(message.getSession(), new HashMap<>());
        commonPool.getUserResponses().get(message.getSession()).put(questions.get(index).getQuestion(), message.getMessage());
        commonPool.addQuestionWithAnswer(message.getSession(), String.valueOf(index), questions.get(index).getQuestion(), message.getMessage());
        log.info("sessionIndex1 {}", sessionIndex.get());


        if (sessionIndex.get() == 1) {
            nextQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());

        }
        if (sessionIndex.get() == 2) {
            Question email = commonPool.getAnswerForQuestion(message.getSession(), "1");
            nextQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), nextQuestion.getQuestion() + email.getAnswer(), nextQuestion.getInputType(), nextQuestion.getArgs());

        }

        if (sessionIndex.get() == 3) {
            Question confirmation = commonPool.getAnswerForQuestion(message.getSession(), "2");
            Question email = commonPool.getAnswerForQuestion(message.getSession(), "1");
            if (confirmation.getAnswer().equals("yes")) {
                boolean isExist = getUserByEmailService.doesUserExist(email.getAnswer());
                log.info("Email already exist {}", isExist);
                if (!isExist) {
                    sessionIndex.set(3);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                    sessionIndex.set(1);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                    commonPool.removeQuestion(message.getSession(), "1");
                } else {
                    sessionIndex.set(4);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                }
            } else {
                sessionIndex.set(1);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                commonPool.removeQuestion(message.getSession(), "1");
                commonPool.removeQuestion(message.getSession(), "2");
            }


        }

        if (sessionIndex.get() == 5) {
            Question email = commonPool.getAnswerForQuestion(message.getSession(), "1");
            Question query = commonPool.getAnswerForQuestion(message.getSession(), "4");
            log.info("llm request sessionId, email, query  {}, {}, {}", message.getSession(), email.getAnswer(), query.getAnswer());
            LLMRequest request = new LLMRequest(message.getSession(), query.getAnswer(), email.getAnswer());
            LLMResponse llmResponse = llmService2.createIssueChain(request);
            if (!llmResponse.isTermination()) {
                sendLLMResponse(llmResponse.getSessionId(), llmResponse.getResponse(), TEXT, null);
            } else {
                sessionIndex.set(-2);
                clearSessionWithSayThanks(message.getSession(), TEXT);
            }

        }
        log.info("sessionIndex {}", sessionIndex.get());


    }

    private void clearSessionWithSayThanks(String session, String type) {
        Message questionMessage = new Message(session, Dictionary.AI, "Thank you for using the Issue reporting Assistant. If you need further assistance, please contact IT support", new Date().getTime(), type, null);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        commonPool.removeSessionData(session);
    }

    private void sendLLMResponse(String session, String question, String type, Object args) {
        Message questionMessage = new Message(session, Dictionary.AI, question, new Date().getTime(), type, args);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        log.info("Sent question to {}: {}", session, question);
    }

    private void sendQuestion(String session, String question, String type, Object args) {
        Message questionMessage = new Message(session, Dictionary.AI, question, new Date().getTime(), type, args);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        log.info("Sent question to {}: {}", session, question);
    }
}
