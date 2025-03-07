package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;

import java.util.List;

public interface IntentWorkflow {
    Message conversation(Message message);

    List<WorkFlowQuestion> getQuestions();
}
