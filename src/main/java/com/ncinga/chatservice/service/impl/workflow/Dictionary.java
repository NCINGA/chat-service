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
}
