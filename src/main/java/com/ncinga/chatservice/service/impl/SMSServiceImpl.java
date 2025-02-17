package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.dto.TokenResponse;
import com.ncinga.chatservice.service.SMSService;
import com.ncinga.chatservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class SMSServiceImpl implements SMSService {
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    @Value("${sms.service.url}")
    private String url;


    public static String generateOTP() {
        String numbers = "1234567890";
        Random random = new Random();
        char[] otp = new char[4];
        for(int i = 0; i< 4 ; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        return String.valueOf(otp);
    }
    @Override
    public boolean send(String number, String otp) {
        try {
            TokenResponse tokenResponse = tokenService.getAuthToken();
            Map<String, Object> payload = new HashMap<>();
            String otpMessage  = "Your OTP is " + otp;
            payload.put("phone_number", number);
            payload.put("message_body", otpMessage);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(tokenResponse.getAccessToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(payload, headers);
            log.info("Request : {}", entity.getBody());
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            log.info("Response : {}", response);
            return true;

        } catch (RestClientException e) {
            log.error("Error during REST call: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
        }


        return false;
    }
}
