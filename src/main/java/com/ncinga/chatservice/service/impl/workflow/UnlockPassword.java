package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.List;

public class UnlockPassword implements IntentWorkflow{
    @Override
    public Message conversation(Message message) {
        return null;
    }

    @Override
    public List<WorkFlowQuestion> getQuestions() {
        return null;
    }
}
