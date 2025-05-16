package com.ncinga.chatservice.service;

public interface EmailService {

    String sendEmail(String to, String body);
    String sendAnotherEmail(String receiver, String body);
}
