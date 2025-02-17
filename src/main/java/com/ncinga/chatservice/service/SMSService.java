package com.ncinga.chatservice.service;

public interface SMSService {
    public boolean send(String number, String otp);
}
