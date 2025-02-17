package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.service.OTPGenerateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service

public class OTPGenerateServiceImpl implements OTPGenerateService {
    private static final String numbers = "1234567890";

    @Override
    public String generateOTP() {

        Random random = new Random();
        char[] otp = new char[4];
        for (int i = 0; i < 4; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        return String.valueOf(otp);
    }
}
