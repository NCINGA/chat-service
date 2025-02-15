package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.Question;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetWorkflow implements WorkflowProcess {
    private final ChatSinkManager<Message> chatSinkManager;
    private final CommonPool commonPool;
    private final List<WorkFlowQuestion> questions;

    private String logUser = "123";
    private String logPassword = "123";

    @Override
    public void execute(AtomicInteger sessionIndex, Message message) throws IllegalAccessException, InterruptedException {
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
            if (message.getMessage().equalsIgnoreCase("ok")) {
                sessionIndex.addAndGet(1);
            } else if (message.getMessage().equalsIgnoreCase("no")) {
                clearSession(message.getSession());
            }

            if (sessionIndex.get() == 2) {
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion());
            }

            if (sessionIndex.get() == 3) {
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion());
            }

            if (sessionIndex.get() == 4) {
                sessionIndex.set(6);
                nextQuestion = questions.get(sessionIndex.get());
                sessionIndex.set(4);
                Question username = commonPool.getAnswerForQuestion(message.getSession(), "2");
                Question password = commonPool.getAnswerForQuestion(message.getSession(), "3");
                log.info("user name and password {} ,{}", username.getAnswer(), password.getAnswer());
                sendQuestion(message.getSession(), nextQuestion.getQuestion());
                if (commonPool.isAuthSuccess() == false && (username.getAnswer().equals(logUser) && password.getAnswer().equals(logPassword))) {
                    log.info("Auth Success..");
                    commonPool.setAuth(true);
                    sessionIndex.set(7);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion());
                    Thread.sleep(5000);
                    sessionIndex.set(4);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion());
                } else {
                    log.info("Auth failed..");
                    sessionIndex.set(8);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion());
                    Thread.sleep(5000);
                    sessionIndex.set(2);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion());
                    commonPool.removeUserResponseData(message.getSession());
                }
            }


            // if (!commonPool.hasQuestionBeenAsked(message.getSession(), nextQuestion.getQuestion())) {
            //      sendQuestion(message.getSession(), nextQuestion.getQuestion());
            // sessionIndex.incrementAndGet();
//            } else {
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion());
//            }
        } else {
            sendQuestion(message.getSession(), "Thank you! Please wait...");
            sendQuestion(message.getSession(), "Password has been changed successfully.");
            log.info("Process completed for session: {}", message.getSession());
            clearSessionWithSayThanks(message.getSession());
        }
    }

    private void clearSession(String session) {
        commonPool.removeSessionData(session);
    }

    private void clearSessionWithSayThanks(String session) {
        Message questionMessage = new Message(session, Dictionary.AI, "Thank you...if you need to any assets say 'hi'", new Date().getTime(), "text");
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        commonPool.removeSessionData(session);
    }

    private void sendQuestion(String session, String question) {
        Message questionMessage = new Message(session, Dictionary.AI, question, new Date().getTime(), "text");
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        log.info("Sent question to {}: {}", session, question);
    }
}
