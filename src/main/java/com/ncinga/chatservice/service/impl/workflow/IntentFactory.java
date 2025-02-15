package com.ncinga.chatservice.service.impl.workflow;


import org.springframework.stereotype.Component;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.EMPLOYEE_ONBOARDING;
import static com.ncinga.chatservice.service.impl.workflow.Dictionary.RESET_PASSWORD;

@Component
public class IntentFactory {

    public static IntentWorkflow getIntent(String workflow) throws IllegalAccessException {
        if (RESET_PASSWORD.equalsIgnoreCase(workflow)) {
            return new PasswordReset();
        } else if (EMPLOYEE_ONBOARDING.equalsIgnoreCase(workflow)) {
            return new EmployeeBoarding();
        }
        return new IllegalAccess();
    }
}
