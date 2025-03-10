package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.Question;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.GetUserByEmailService;
import com.ncinga.chatservice.service.UnlockUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.TEXT;

@Service
@Slf4j
@AllArgsConstructor
public class UnlockUserWorkflow implements WorkflowProcess{

    private final ChatSinkManager<Message> chatSinkManager;
    private final CommonPool commonPool;
    private final List<WorkFlowQuestion> questions;
    private final UnlockUserService unlockUserService;
    private final GetUserByEmailService getUserByEmailService;


    @Override
    public void execute(AtomicInteger sessionIndex, Message message) {
        int index = sessionIndex.get();
        WorkFlowQuestion nextQuestion;
        if (index >= questions.size() || index == -1) {
            log.info("All questions answered or session not started for user: {}", message.getSession());
            return;
        }

        log.info("message : {}", message);
        commonPool.getUserResponses().putIfAbsent(message.getSession(), new HashMap<>());
        commonPool.getUserResponses().get(message.getSession()).put(questions.get(index).getQuestion(), message.getMessage());
        commonPool.addQuestionWithAnswer(message.getSession(), String.valueOf(index), questions.get(index).getQuestion(), message.getMessage());

        if (index + 1 < questions.size()) {
            sessionIndex.incrementAndGet();
            if (message.getMessage().equalsIgnoreCase("yes")) {
                sessionIndex.addAndGet(1);
            } else if (message.getMessage().equalsIgnoreCase("no")) {
                clearSession(message.getSession());
            }

            if (sessionIndex.get() == 2) {
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
            }

            if (sessionIndex.get() == 3) {
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
            }

            if (sessionIndex.get() == 4) {
                sessionIndex.set(6);
                nextQuestion = questions.get(sessionIndex.get());
                sessionIndex.set(4);
                Question username = commonPool.getAnswerForQuestion(message.getSession(), "2");
                Question password = commonPool.getAnswerForQuestion(message.getSession(), "3");
                log.info("user name and password {} ,{}", username.getAnswer(), password.getAnswer());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                if (!commonPool.isAuthSuccess()) {
                    log.info("Auth Success..");
                    commonPool.setAuth(true);
                    sessionIndex.set(7);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                    sessionIndex.set(4);
                } else {
                    log.info("Auth failed..");
                    sessionIndex.set(8);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                    sessionIndex.set(2);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                    commonPool.removeUserResponseData(message.getSession());
                }
            }

            if (sessionIndex.get() == 4) {
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
            }

            if (sessionIndex.get() == 5) {
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                log.info("Unlocking user account!");

                Question email = commonPool.getAnswerForQuestion(message.getSession(), "4");
                String userId = getUserByEmailService.getUserIdByEmail(email.getAnswer());
                String response = unlockUserService.enableUserAccount(userId);
                log.info("Response : {}", response);
            }

            if (sessionIndex.get() == 6) {
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                clearSessionWithSayThanks(message.getMessage(), TEXT);
            }
        }
    }

    private void clearSession(String session) {
        commonPool.removeSessionData(session);
    }

    private void clearSessionWithSayThanks(String session, String type) {
        Message questionMessage = new Message(session, Dictionary.AI, "Thank you...if you need to any assets say 'hi'", new Date().getTime(), type);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        commonPool.removeSessionData(session);
    }

    private void sendQuestion(String session, String question, String type) {
        Message questionMessage = new Message(session, Dictionary.AI, question, new Date().getTime(), type);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        log.info("Sent question to {}: {}", session, question);
    }
}
