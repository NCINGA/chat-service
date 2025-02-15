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

    @Override
    public void execute(AtomicInteger sessionIndex, Message message) throws IllegalAccessException {
        int index = sessionIndex.get();
        if (index >= questions.size() || index == -1) {
            log.info("All questions answered or session not started for user: {}", message.getSession());
            return;
        }

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
            WorkFlowQuestion nextQuestion;
            nextQuestion = questions.get(sessionIndex.get());
            if (!commonPool.hasQuestionBeenAsked(message.getSession(), nextQuestion.getQuestion())) {
                sendQuestion(message.getSession(), nextQuestion.getQuestion());
            } else {
                sessionIndex.addAndGet(1);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion());

            }
        } else {
            sendQuestion(message.getSession(), "Thank you! Please wait...");
            sendQuestion(message.getSession(), "Password has been changed successfully.");
            log.info("Process completed for session: {}", message.getSession());
            clearSessionWithSayThanks(message.getSession());
        }
        Question username = commonPool.getAnswerForQuestion(message.getSession(), "2");
        Question password = commonPool.getAnswerForQuestion(message.getSession(), "3");

        if (username != null && username.getAnswer() != null && password != null && password.getAnswer() != null) {
            log.info("Entered username and password: {}, {}", username.getAnswer(), password.getAnswer());
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
