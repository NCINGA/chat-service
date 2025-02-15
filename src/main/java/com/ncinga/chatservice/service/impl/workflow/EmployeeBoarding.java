package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.InputTypes;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.RequiredTypes;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.ArrayList;
import java.util.List;

public class EmployeeBoarding implements IntentWorkflow {
    @Override
    public Message conversation(Message message) {
        return new Message(null,null, null, 0, null);
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        List<WorkFlowQuestion> questions = new ArrayList<>();
        questions.add(new WorkFlowQuestion("Got it... To continue this process login is required", RequiredTypes.AUTHENTICATION, InputTypes.YES_NO, -1, -1));
        questions.add(new WorkFlowQuestion("Enter employee first name", RequiredTypes.AUTHENTICATION, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("Enter employee middle name", RequiredTypes.AUTHENTICATION, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("Enter employee first name", RequiredTypes.AUTHENTICATION, InputTypes.TEXT, -1, -1));
        questions.add(new WorkFlowQuestion("Enter employee email address", RequiredTypes.AUTHENTICATION, InputTypes.TEXT, -1, -1));

        return questions;
    }
}
