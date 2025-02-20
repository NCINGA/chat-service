package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.*;

import java.util.List;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.REPORT_ISSUE;
import static com.ncinga.chatservice.service.impl.workflow.Dictionary.RESET_PASSWORD;

public class WorkflowProcessFactory {

    public static WorkflowProcess getWorkflowProcess(String workflow, ChatSinkManager<Message> chatSinkManager, CommonPool commonPool, List<WorkFlowQuestion> questions, PasswordResetService passwordResetService, SMSService smsService, GetUserByEmailService getUserByEmailService, OTPGenerateService otpGenerateService, UserService userService, LLMService2 llmService2)throws

    IllegalAccessException {
        if (RESET_PASSWORD.equalsIgnoreCase(workflow)) {
            return new PasswordResetWorkflow(chatSinkManager, commonPool, questions, passwordResetService, getUserByEmailService, smsService, otpGenerateService, userService);
        } else if (REPORT_ISSUE.equalsIgnoreCase(workflow)) {
            return new CreateTicketWorkflow(chatSinkManager, commonPool, questions, passwordResetService, getUserByEmailService, smsService, otpGenerateService, userService, llmService2);
        }
        return null;
    }

}
