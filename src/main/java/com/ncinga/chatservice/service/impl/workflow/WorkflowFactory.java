package com.ncinga.chatservice.service.impl.workflow;


import org.springframework.stereotype.Component;

@Component
public class WorkflowFactory {

    public static Workflow getWorkflow(Workflows workflow) throws IllegalAccessException {
        if (workflow.equals(Workflows.RESET_PASSWORD)) {
            return new PasswordReset();
        } else if (workflow.equals(Workflows.USER_ONBOARD)) {
            return new PasswordReset();
        }
        throw new IllegalAccessException("Workflow request error");
    }
}
