package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.LLMService;

import java.util.List;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.EMPLOYEE_ONBOARDING;
import static com.ncinga.chatservice.service.impl.workflow.Dictionary.RESET_PASSWORD;

public class WorkflowProcessFactory {

    public static WorkflowProcess getWorkflowProcess(String workflow, ChatSinkManager<Message> chatSinkManager, CommonPool commonPool, List<WorkFlowQuestion> questions) throws IllegalAccessException {
        if (RESET_PASSWORD.equalsIgnoreCase(workflow)) {
            return new PasswordResetWorkflow(chatSinkManager, commonPool, questions);
        } else if (EMPLOYEE_ONBOARDING.equalsIgnoreCase(workflow)) {
            return null;
        }
        return null;
    }

}
