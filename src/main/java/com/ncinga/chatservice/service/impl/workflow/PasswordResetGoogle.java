package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.ArrayList;
import java.util.List;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.TEXT;
import static com.ncinga.chatservice.service.impl.workflow.Dictionary.YES_NO;

public class PasswordResetGoogle implements IntentWorkflow {

    @Override
    public Message conversation(Message message) {
        return new Message(null, null, null, 0, null);
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        List<WorkFlowQuestion> questions = new ArrayList<>();
        questions.add(new WorkFlowQuestion("Please enter the email of the user you wish to change the password of", TEXT));
        questions.add(new WorkFlowQuestion("Are you sure you want to change the password of this user", YES_NO));
        questions.add(new WorkFlowQuestion("Processing....", TEXT));
        return questions;

    }
}
