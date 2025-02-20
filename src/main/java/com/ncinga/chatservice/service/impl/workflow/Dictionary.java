package com.ncinga.chatservice.service.impl.workflow;

import java.util.Arrays;
import java.util.List;

public abstract class Dictionary {
    public static final List<String> GREETS = Arrays.asList("hi", "hey", "dude", "ai", "");
    public static final String AI = "AI";
    public static final List<String> GREETING_MESSAGE = Arrays.asList(
            "Hello! How can I assist you today?",
            "Welcome! How may I help you?",
            "Good day! How can I be of service?",
            "Greetings! How can I support you today?",
            "Hello! Let me know how I can assist you.",
            "Welcome! I’m here to help. What do you need assistance with?",
            "Good to see you! How can I assist you?",
            "Hello! Feel free to ask me anything.",
            "Welcome! How can I make your day easier?",
            "Good day! I’m here to help. What can I do for you?",
            "Hello! Please let me know how I can assist you.",
            "Welcome! I’m ready to assist you. What would you like help with?",
            "Good day! How can I provide support for you today?",
            "Greetings! Let me know what you need, and I’ll do my best to assist.",
            "Hello! I’m here to make things easier for you. How can I help?",
            "Welcome! Please feel free to ask me anything.",
            "Good day! What do you need assistance with today?",
            "Hello! Let’s get started. How may I assist you?",
            "Welcome! I’m happy to help. How can I assist you today?",
            "Greetings! What can I do for you?"

    );
    public static final String RESET_PASSWORD = "reset_password";
    public static final String UNLOCK_MICROSOFT_ACCOUNT = "unlock_microsoft_account";
    public static final String KB_ARTICLE_ASSISTANCE = "kb_article_assistance";
    public static final String EMPLOYEE_ONBOARDING = "employee_onboarding";
    public static final String EMPLOYEE_OFFBOARDING = "employee_offboarding";
    public static final String GROUP_EMAIL_MANAGEMENT = "group_email_management";
    public static final String REPORT_ISSUE = "report_issue";
    public static final String TEXT = "text";
    public static final String LIST = "list";
    public static final String PASSWORD = "password";
    public static final String PROBLEM = "problem";
    public static final String YES_NO = "yes_no";
    public static final String YES = "yes";
    public static final String NO = "no";
}
