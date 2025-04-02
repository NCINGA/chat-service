package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.Question;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.GoogleOperationsService;
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
public class GooglePasswordResetWorkflow implements WorkflowProcess{
    private final ChatSinkManager<Message> chatSinkManager;
    private final CommonPool commonPool;
    private final List<WorkFlowQuestion> questions;
    private final GoogleOperationsService googleOperationsService;

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

        if (index == 2) {
            if (message.getMessage().equalsIgnoreCase("yes")) {
                // Set index to the "Processing" message
                sessionIndex.set(3);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());

                // Get the email to reset password from previous question
                Question emailQuestion = commonPool.getAnswerForQuestion(message.getSession(), "1");
                log.info("Resetting password for user: {}", emailQuestion.getAnswer());
                String response = googleOperationsService.resetUserPassword(emailQuestion.getAnswer());

                // Send the result and thank you message
                sendQuestion(message.getSession(), response, TEXT);
                clearSessionWithSayThanks(message.getSession(), TEXT);
                return;
            } else if (message.getMessage().equalsIgnoreCase("no")) {
                // If user says no, end the workflow with thank you
                clearSessionWithSayThanks(message.getSession(), TEXT);
                return;
            } else {
                // If response is neither yes nor no, ask again
                sendQuestion(message.getSession(), "Please respond with 'yes' or 'no'. Are you sure you want to change the password of this user?", TEXT);
                return;
            }
        }

        // Continue to next question if there are more
        if (index + 1 < questions.size()) {
            sessionIndex.incrementAndGet();
            nextQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
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
