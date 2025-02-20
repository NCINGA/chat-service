package com.ncinga.chatservice.service.impl.workflow;


import org.springframework.stereotype.Component;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.*;

@Component
public class IntentFactory {

    public static IntentWorkflow getIntent(String workflow) throws IllegalAccessException {
        if (RESET_PASSWORD.equalsIgnoreCase(workflow)) {
            return new PasswordReset();
        } else if (REPORT_ISSUE.equalsIgnoreCase(workflow)) {
            return new CreateTicket();
        }
        return new IllegalAccess();
    }
}
