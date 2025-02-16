package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.ArrayList;
import java.util.Arrays;
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
        questions.add(new WorkFlowQuestion("Are you admin user or application user?", LIST, Arrays.asList("admin", "application")));
        questions.add(new WorkFlowQuestion("You selected ", TEXT, null));
        questions.add(new WorkFlowQuestion("We will send OTP", TEXT, null));
        questions.add(new WorkFlowQuestion("Please enter validate mobile number", TEXT, null));
        questions.add(new WorkFlowQuestion("Your contact number is ", TEXT, null));
        questions.add(new WorkFlowQuestion("OTP has been sent please check mobile", TEXT, null));
        questions.add(new WorkFlowQuestion("Please enter OTP number", TEXT, null));
        questions.add(new WorkFlowQuestion("Verified...", TEXT, null));
        questions.add(new WorkFlowQuestion("Verification failed...", PROBLEM, null));
        questions.add(new WorkFlowQuestion("Please enter your username or object ID", TEXT, null));
        questions.add(new WorkFlowQuestion("Please enter your validate password", TEXT, null));
        questions.add(new WorkFlowQuestion("Working...", TEXT, null));
        return questions;
    }
}
