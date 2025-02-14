package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;

import java.util.ArrayList;
import java.util.List;


public class PasswordReset implements Workflow {
    @Override
    public Message conversation(Message message) {
        return new Message(null, null, 0, null);
    }

    @Override
    public List<String> getQuestions() {
        List<String> questions = new ArrayList<>();
        questions.add("What is your email address?");
        questions.add("Tel me your new password:");
        return questions;
    }
}
