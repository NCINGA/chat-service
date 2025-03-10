package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.ArrayList;
import java.util.List;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.TEXT;
import static com.ncinga.chatservice.service.impl.workflow.Dictionary.YES_NO;

public class UserOnBoarding implements IntentWorkflow{

    @Override
    public Message conversation(Message message){
        return new Message(null, null, null, 0, null);
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        List<WorkFlowQuestion> questions = new ArrayList<>();
        questions.add(new WorkFlowQuestion("Got it...To continue this operation, please login", YES_NO));
        questions.add(new WorkFlowQuestion("Thank you!", TEXT));
        questions.add(new WorkFlowQuestion("What is your username?", TEXT));
        questions.add(new WorkFlowQuestion("What is your password?", TEXT));
        questions.add(new WorkFlowQuestion("Please provide the details of the user you want to create", TEXT));
        questions.add(new WorkFlowQuestion("Username?", TEXT));
        questions.add(new WorkFlowQuestion("Nickname?", TEXT));
        questions.add(new WorkFlowQuestion("Email?", TEXT));
        questions.add(new WorkFlowQuestion("Password?", TEXT));
        questions.add(new WorkFlowQuestion("Contact number?", TEXT));
        questions.add(new WorkFlowQuestion("Creating a new user with the information you provided........", TEXT));
        questions.add(new WorkFlowQuestion("User created successfully!", TEXT));
        questions.add(new WorkFlowQuestion("User on-boarding failed! Please try again...", TEXT));
        return questions;
    }
}
