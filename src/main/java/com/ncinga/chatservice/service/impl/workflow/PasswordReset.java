package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.InputTypes;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.RequiredTypes;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.ArrayList;
import java.util.List;


public class PasswordReset implements IntentWorkflow {
    @Override
    public Message conversation(Message message) {
        return new Message(null, null, null, 0, null);
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        List<WorkFlowQuestion> questions = new ArrayList<>();
        questions.add(new WorkFlowQuestion("Got it... To continue this operation login is required", RequiredTypes.AUTHENTICATION, InputTypes.YES_NO, 2, 1));
        questions.add(new WorkFlowQuestion("Thank You...", RequiredTypes.NONE, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("Tell, me what is your username?", RequiredTypes.NONE, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("What is your password ?", RequiredTypes.NONE, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("Enter password change username : ", RequiredTypes.NONE, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("Enter new password : ", RequiredTypes.NONE, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("Authenticating......", RequiredTypes.NONE, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("Authenticating Success", RequiredTypes.NONE, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("Authenticating Failed", RequiredTypes.NONE, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("Processing...", RequiredTypes.NONE, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("Password has been changed....", RequiredTypes.NONE, InputTypes.TEXT, -1, -1));
        return questions;
    }
}
