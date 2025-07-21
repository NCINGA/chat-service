package com.ncinga.chatservice.service.impl.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.EMPLOYEE_ONBOARDING;
import static com.ncinga.chatservice.service.impl.workflow.Dictionary.RESET_PASSWORD;

@Component
public class IntentFactory {

    private static FaqQuery faqQuery;

    @Autowired
    public void setFaqQuery(FaqQuery faqQuery) {
        IntentFactory.faqQuery = faqQuery;
    }

    public static IntentWorkflow getIntent(String workflow) {
        if (RESET_PASSWORD.equalsIgnoreCase(workflow)) {
            return new PasswordReset();
        } else if (EMPLOYEE_ONBOARDING.equalsIgnoreCase(workflow)) {
            return new EmployeeBoarding();
        }
        return faqQuery;
    }
}
