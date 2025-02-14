package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;

import java.util.List;
import java.util.Map;

public interface Workflow {
    Message conversation(Message message);

    List<String>  getQuestions();
}
