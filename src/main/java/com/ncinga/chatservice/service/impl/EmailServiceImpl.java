package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.dto.EmailRequestDto;
import com.ncinga.chatservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${email.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();


    public String sendEmail(String to, String body) {

        String subject = "Ncinga Helpdesk Password Reset";
        String sender = "gayan.dissanayake@ncinga.net";
        String ccEmail = "damiru.399@gmail.com";

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(sender);
        message.setTo(to);
        message.setCc(ccEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        return "Email send sucessfully!";
    }

    public String sendAnotherEmail(String receiver, String body) {
        String senderName = "Ncinga Helpdesk";
        String senderEmail = "support@ncinga.net";
        String subject = "Ncinga Helpdesk Password Reset";
        String url = "https://api.sendinblue.com/v3/smtp/email";

        Map<String, String> sender = new HashMap<>();
        sender.put("name", senderName);
        sender.put("email", senderEmail);

        List<Map<String, String>> to = new ArrayList<>();
        Map<String, String> recipient = new HashMap<>();
        recipient.put("email", receiver);
        to.add(recipient);

        List<Map<String, String>> cc = new ArrayList<>();
        Map<String, String> ccEmail = new HashMap<>();
        ccEmail.put("email", "damiru.399@gmail.com");
        cc.add(ccEmail);

        EmailRequestDto emailRequest = new EmailRequestDto(sender, to, cc, subject, body);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<EmailRequestDto> request = new HttpEntity<>(emailRequest, headers);

        ResponseEntity<String> response= restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response.getBody();
    }

}
