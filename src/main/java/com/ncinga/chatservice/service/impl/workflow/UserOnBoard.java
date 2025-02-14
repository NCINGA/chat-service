package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;

import java.util.List;
import java.util.Map;

public class UserOnBoard implements Workflow {
    @Override
    public Message conversation(Message message) {
        return new Message(null, null, 0, null);
    }

    @Override
    public List<String>  getQuestions() {
        return null;
    }
}
