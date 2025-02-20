package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.ArrayList;
import java.util.List;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.TEXT;
import static com.ncinga.chatservice.service.impl.workflow.Dictionary.YES_NO;

public class CreateTicket implements IntentWorkflow {
    @Override
    public Message conversation(Message message) {
        return null;
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        List<WorkFlowQuestion> questions = new ArrayList<>();
        questions.add(new WorkFlowQuestion("I’ll need some details before creating your ticket. Let’s get started!", TEXT, null));
        questions.add(new WorkFlowQuestion("Please provide your email ", TEXT, null));
        questions.add(new WorkFlowQuestion("Please confirm if this is correct  ", YES_NO, null));
        questions.add(new WorkFlowQuestion("Given email not valid one please check and re enter ", TEXT, null));
        questions.add(new WorkFlowQuestion("What is your query/issue?", TEXT, null));
        questions.add(new WorkFlowQuestion("Processing...", TEXT, null));

        return questions;
    }
}
