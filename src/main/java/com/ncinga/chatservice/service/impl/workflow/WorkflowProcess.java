package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Message;

import java.util.concurrent.atomic.AtomicInteger;

public interface WorkflowProcess {
    public void execute(AtomicInteger sessionIndex, Message message) throws IllegalAccessException;
}
