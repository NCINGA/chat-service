package com.ncinga.chatservice.service.impl;


import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.service.ChatService;
import com.ncinga.chatservice.service.impl.workflow.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatSinkManager<Message> chatSinkManager;
    private final CommonPool commonPool;


    @Override
    public void sendMessage(Message message) throws IllegalAccessException, InterruptedException {
        log.info("Chat message received from user: {}", message.getUser());
        AtomicInteger userIndex = commonPool.getUserIndex(message.getUser());

        Workflow workflow = WorkflowFactory.getWorkflow(Workflows.RESET_PASSWORD);

        List<String> questions = workflow.getQuestions();
        if (Dictionary.GREETS.contains(message.getMessage().toLowerCase())) {
            if (userIndex.get() == -1) {
                userIndex.set(-2);
                String randomGreeting = Dictionary.GREETING_MESSAGE.get(
                        ThreadLocalRandom.current().nextInt(Dictionary.GREETING_MESSAGE.size())
                );
                Message greeting = new Message(Dictionary.AI, randomGreeting, new Date().getTime(), "text");
                chatSinkManager.getChatSink().get(message.getUser()).tryEmitNext(greeting);
                log.info("Greeting sent to user.");
                return;
            } else {
                log.info("User already in conversation, ignoring duplicate 'hi'.");
                return;
            }
        }
        if (userIndex.get() == -2) {
            userIndex.set(0);
            sendQuestion(message.getUser(), userIndex.get(), questions);
            return;
        }
        int index = userIndex.get();
        if (index >= questions.size() || index == -1) {
            log.info("All questions answered or session not started for user: {}", message.getUser());
            return;
        }
        commonPool.getUserResponses().putIfAbsent(message.getUser(), new HashMap<>());
        commonPool.getUserResponses().get(message.getUser()).put(questions.get(index), message.getMessage());
        commonPool.addQuestionWithAnswer(message.getUser(), String.valueOf(index), questions.get(index), message.getMessage());

        if (index + 1 < questions.size()) {
            userIndex.incrementAndGet();
            sendQuestion(message.getUser(), userIndex.get(), questions);
        } else {
            Message thankyou = new Message("AI", "Thank you just wait...", new Date().getTime(), "text");
            chatSinkManager.getChatSink().get(message.getUser()).tryEmitNext(thankyou);
            Thread.sleep(3000);
            Message status = new Message(Dictionary.AI, "Password has been change successfully....", new Date().getTime(), "text");
            chatSinkManager.getChatSink().get(message.getUser()).tryEmitNext(status);
            log.info("Process done");
        }
    }


    private void sendQuestion(String user, int index, List<String> questions) {
        String nextQuestion = questions.get(index);
        Message question = new Message(Dictionary.AI, nextQuestion, new Date().getTime(), "text");
        chatSinkManager.getChatSink().get(user).tryEmitNext(question);
        log.info("Next question sent to user {}: {}", user, nextQuestion);
    }

}
