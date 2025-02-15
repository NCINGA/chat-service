package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.ArrayList;
import java.util.List;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.TEXT;
import static com.ncinga.chatservice.service.impl.workflow.Dictionary.YES_NO;


public class PasswordReset implements IntentWorkflow {
    @Override
    public Message conversation(Message message) {
        return new Message(null, null, null, 0, null);
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        List<WorkFlowQuestion> questions = new ArrayList<>();
        questions.add(new WorkFlowQuestion("Got it... To continue this operation login is required", YES_NO));
        questions.add(new WorkFlowQuestion("Thank You...", TEXT));
        questions.add(new WorkFlowQuestion("Tell, me what is your username?", TEXT));
        questions.add(new WorkFlowQuestion("What is your password ?", TEXT));
        questions.add(new WorkFlowQuestion("Enter password change username : ", TEXT));
        questions.add(new WorkFlowQuestion("Enter new password : ", TEXT));
        questions.add(new WorkFlowQuestion("Authenticating......", TEXT));
        questions.add(new WorkFlowQuestion("Authenticating Success", TEXT));
        questions.add(new WorkFlowQuestion("Authenticating Failed", TEXT));
        questions.add(new WorkFlowQuestion("Processing...", TEXT));
        return questions;
    }
}
