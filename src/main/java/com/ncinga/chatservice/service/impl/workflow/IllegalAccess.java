package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.InputTypes;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.RequiredTypes;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.ArrayList;
import java.util.List;

public class IllegalAccess implements IntentWorkflow {
    @Override
    public Message conversation(Message message) {
        return new Message(null, null, null, 0, null);
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        List<WorkFlowQuestion> questions = new ArrayList<>();
        questions.add(new WorkFlowQuestion("I'm sorry, but I didn't quite understand your request. if you need to any assist say 'hi' ?", RequiredTypes.AUTHENTICATION, InputTypes.TEXT, -1, -1));
        return questions;
    }
}
