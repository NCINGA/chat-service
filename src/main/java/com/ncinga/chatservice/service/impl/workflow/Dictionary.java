package com.ncinga.chatservice.service.impl.workflow;

import java.util.Arrays;
import java.util.List;

public abstract class Dictionary {
    public static final List<String> GREETS = Arrays.asList("hi", "hey", "dude", "ai", "");
    public static final String AI = "AI";
    public static final List<String> GREETING_MESSAGE = Arrays.asList(
            "Hey there! How can I assist you today?",
            "Hi! What’s on your mind?",
            "Hello! How may I assist you?",
            "Greetings! How can I help you today?",
            "Yo! What’s up? Need some help?",
            "Hello, my friend! How can I assist?",
            "Hi, human! Ready to chat?",
            "Hey! How can I help?"
    );
    public static final String RESET_PASSWORD = "reset_password";
    public static final String UNLOCK_MICROSOFT_ACCOUNT = "unlock_microsoft_account";
    public static final String KB_ARTICLE_ASSISTANCE = "kb_article_assistance";
    public static final String EMPLOYEE_ONBOARDING = "employee_onboarding";
    public static final String EMPLOYEE_OFFBOARDING = "employee_offboarding";
    public static final String GROUP_EMAIL_MANAGEMENT = "group_email_management";
    public static final String TEXT = "text";
    public static final String YES_NO = "yes_no";
    public static final String YES = "yes";
    public static final String NO = "no";
}
