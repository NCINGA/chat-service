package com.ncinga.chatservice.service.impl.workflow;


import org.springframework.stereotype.Component;

@Component
public class WorkflowFactory {

    public static Workflow getWorkflow(String workflow) throws IllegalAccessException {
        if (workflow.equals(workflow)) {
            return new PasswordReset();
        } else if (workflow.equals(workflow)) {
            return new PasswordReset();
        }
        throw new IllegalAccessException("Workflow request error");
    }
}
