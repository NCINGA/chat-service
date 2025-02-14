package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;

import java.util.List;

public interface Workflow {
    Message conversation(Message message);

    List<String>  getQuestions();
}
