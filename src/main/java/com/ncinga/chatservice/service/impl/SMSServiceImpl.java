package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.service.SMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class SMSServiceImpl implements SMSService {
    private final RestTemplate restTemplate;

    @Value("${sms.service.url}")
    private String url;

    @Value("${sms.service.url}")
    private String apiUrl;

    @Value("${sms.server.key}")
    private String serverKey;

    @Override
    public String generateOTP() {
        String numbers = "1234567890";
        Random random = new Random();
        char[] otp = new char[4];
        for(int i = 0; i< 4 ; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        return String.valueOf(otp);
    }

    @Override
    public String sendOtp(String number) {
        try{
            String otp = generateOTP();
            String message = "Dear User, please use the following One-Time Password (OTP) to recover your NCINGA email account " + otp;

            String fullUrl = apiUrl +
                    "?esmsqk=" + serverKey +
                    "&list=" + number +
                    "&message=" + message;

            // Send the GET request
            ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);

            // Check the response
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("SMS sent successfully!");
                System.out.println("Response: " + response.getBody());
            } else {
                System.out.println("Failed to send SMS.");
                System.out.println("Status code: " + response.getStatusCodeValue());
                System.out.println("Response: " + response.getBody());
            }

            log.info("Response : {}", response.getBody());
            return otp;
        }catch (RestClientException e){
            log.error("Error during REST call: {}", e.getMessage());
        }
        return "error";
    }

    @Override
    public String sendMessage(String number, String message1) {
        try{

            String fullUrl = apiUrl +
                    "?esmsqk=" + serverKey +
                    "&list=" + number +
                    "&message=" + message1;

            // Send the GET request
            ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);

            // Check the response
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("SMS sent successfully!");
                System.out.println("Response: " + response.getBody());
            } else {
                System.out.println("Failed to send SMS.");
                System.out.println("Status code: " + response.getStatusCodeValue());
                System.out.println("Response: " + response.getBody());
            }

            log.info("Response : {}", response.getBody());
            return message1;
        }catch (RestClientException e){
            log.error("Error during REST call: {}", e.getMessage());

   }
        return "error";
    }

}
