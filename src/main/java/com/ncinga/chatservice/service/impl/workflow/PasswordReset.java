package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.ArrayList;
import java.util.List;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.*;


public class PasswordReset implements IntentWorkflow {
    @Override
    public Message conversation(Message message) {
        return new Message(null, null, null, 0, null, null);
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        List<WorkFlowQuestion> questions = new ArrayList<>();
        questions.add(new WorkFlowQuestion("Got it... To continue this operation login is required", YES_NO, null));
        questions.add(new WorkFlowQuestion("Thank You...", TEXT, null));
        questions.add(new WorkFlowQuestion("Tell, me what is your username?", TEXT, null));
        questions.add(new WorkFlowQuestion("What is your password ?", PASSWORD, null));
        questions.add(new WorkFlowQuestion("Enter password change username : ", TEXT, null));
        questions.add(new WorkFlowQuestion("Enter new password : ", PASSWORD, null));
        questions.add(new WorkFlowQuestion("Authenticating......", TEXT, null));
        questions.add(new WorkFlowQuestion("Authenticating Success", TEXT, null));
        questions.add(new WorkFlowQuestion("Authenticating Failed", TEXT, null));
        questions.add(new WorkFlowQuestion("Processing...", TEXT, null));
        questions.add(new WorkFlowQuestion("Are you admin user or application user?", LIST, List.of("admin", "application")));
        return questions;
    }
}
