package com.ncinga.chatservice.service;

public interface SMSService {
    public String send(String number);

    public String generateOTP();

    String sendMessage(String number, String message);
}
