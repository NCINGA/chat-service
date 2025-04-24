package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.*;

import java.util.List;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.*;

public class WorkflowProcessFactory {

    public static WorkflowProcess getWorkflowProcess(LLMService llmService,String workflow, ChatSinkManager<Message> chatSinkManager, CommonPool commonPool, List<WorkFlowQuestion> questions,UserOnBoardingService userOnBoardingService, UserOffBoardingService userOffBoardingService, UnlockUserService unlockUserService, GetUserByEmailService getUserByEmailService, GoogleOperationsService googleOperationsService, SMSService smsService) throws IllegalAccessException {
        if (RESET_PASSWORD.equalsIgnoreCase(workflow)) {
            return new PasswordResetWorkflow(chatSinkManager, commonPool, questions, googleOperationsService, smsService);
        } else if (EMPLOYEE_ONBOARDING.equalsIgnoreCase(workflow)) {
            return new UserOnBoardingWorkflow(chatSinkManager, commonPool, questions, userOnBoardingService);
        } else if (EMPLOYEE_OFFBOARDING.equalsIgnoreCase(workflow)) {
            return new UserOffBoardingWorkflow(chatSinkManager, commonPool, questions, userOffBoardingService, getUserByEmailService);
        } else if (UNLOCK_MICROSOFT_ACCOUNT.equalsIgnoreCase(workflow)) {
            return new UnlockUserWorkflow(chatSinkManager, commonPool, questions, unlockUserService, getUserByEmailService);
        }
        return new GeneralProcess(chatSinkManager, llmService);
    }
}
