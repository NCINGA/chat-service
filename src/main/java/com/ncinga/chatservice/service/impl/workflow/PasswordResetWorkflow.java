package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.GoogleRequestUserDto;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.Question;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.EmailService;
import com.ncinga.chatservice.service.GoogleOperationsService;
import com.ncinga.chatservice.service.PasswordResetService;
import com.ncinga.chatservice.service.SMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ncinga.chatservice.service.impl.workflow.Dictionary.TEXT;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetWorkflow implements WorkflowProcess {
    private final ChatSinkManager<Message> chatSinkManager;
    private final CommonPool commonPool;
    private final List<WorkFlowQuestion> questions;
    private final GoogleOperationsService googleOperationsService;
    private final SMSService smsService;
    private final static String ACCOUNT_SUSPENDED = "Suspended";
    private final static long OTP_EXPIRATION_TIME = 2 * 60 * 1000;
    private final EmailService emailService;
    String emailSender = "gayan.dissanayake@ncinga.net";

    @Override
    public void execute(AtomicInteger sessionIndex, Message message) {
        int index = sessionIndex.get();
        WorkFlowQuestion nextQuestion;
        if (index >= questions.size() || index == -1) {
            log.info("All questions answered or session not started for user: {}", message.getSession());
            return;
        }

        log.info("message : {}", message);
        commonPool.getUserResponses().putIfAbsent(message.getSession(), new HashMap<>());
        commonPool.getUserResponses().get(message.getSession()).put(questions.get(index).getQuestion(), message.getMessage());

        if(index == 0) {
            String email =  message.getMessage().trim();
            log.info("Email : {}", email);

            commonPool.addEmail(message.getSession(), email);
            String tempEmail = commonPool.getEmail(message.getSession());
            log.info("Email : {}", tempEmail);

            GoogleRequestUserDto user = googleOperationsService.getUserInfo(email);

            if(user == null) {
                sessionIndex.set(5);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());

                sessionIndex.set(0);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());

                String correctEmail = message.getMessage();
                commonPool.removeEmail(message.getSession());
                log.info("Old email : {}", commonPool.getEmail(message.getSession()));
                commonPool.addEmail(message.getSession(), correctEmail);
                String newEmail = commonPool.getEmail(message.getSession());
                log.info("Email : {}", newEmail);

            } else {
                if (user.getStatus() != null && user.getStatus().equals(ACCOUNT_SUSPENDED)) {
                    sessionIndex.set(6);

                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                    log.info("Account suspended");

                    clearSessionWithSayThanks(message.getSession(), TEXT);

                    return;
                }

                String number = user.getPhoneNumber();

                if (number == null) {
                    sessionIndex.set(13);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                    String body = "Your password reset attempt failed. Could not find a valid phone number linked to your account.";
                    String to = commonPool.getEmail(message.getSession());
                    confirmationEmail(emailSender, to, body);
                    clearSessionWithSayThanks(message.getSession(), TEXT);
                }
                log.info("Phone number : {}", number);
                String otp = smsService.sendOtp(number);
                log.info("OTP : {}", otp);
                commonPool.addOTP(message.getSession(), otp);
                commonPool.addOTPTimestamp(message.getSession(), System.currentTimeMillis());

                sessionIndex.incrementAndGet();
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                commonPool.addQuestionWithAnswer(message.getSession(), String.valueOf(index), questions.get(index).getQuestion(), message.getMessage());
            }
        }

        if(index == 1){
            String enteredOTP = message.getMessage().trim();
            commonPool.addInputOTP(message.getSession(), enteredOTP);
            String inputOTP = commonPool.getInputOTP(message.getSession());
            log.info("Input OTP : {}", inputOTP);

            String generatedOTP = commonPool.getOTP(message.getSession());
            log.info("Generated OTP : {}", generatedOTP);

            long otpTimestamp = commonPool.getOTPTimestamp(message.getSession());
            long currentTime = System.currentTimeMillis();

            if (currentTime - otpTimestamp > OTP_EXPIRATION_TIME) {
                // OTP has expired
                log.info("OTP expired for session: {}", message.getSession());
                sessionIndex.set(11); // New question index for OTP expiration
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                String body = "Your password reset attempt failed. Session expired.";
                String to = commonPool.getEmail(message.getSession());
                confirmationEmail(emailSender, to, body);
                clearSessionWithSayThanks(message.getSession(), TEXT);
                return;
            }

            if (generatedOTP.equals(inputOTP)) {
                log.info("OTP verified!");
                sessionIndex.set(3);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());

                Question email = commonPool.getAnswerForQuestion(message.getSession(), "0");

                if(email != null) {
                    String response = googleOperationsService.resetUserPassword(email.getAnswer());
                    log.info("Response : {}", response);

                    GoogleRequestUserDto user = googleOperationsService.getUserInfo(email.getAnswer());
                    String phoneNumber = user.getPhoneNumber();
                    smsService.sendMessage(phoneNumber, response);
                    commonPool.addResponse(message.getSession(), response);


                    sessionIndex.set(9);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());

                    sessionIndex.set(12);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());

                    sessionIndex.set(10);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());


                } else {

                    String correctEmail = commonPool.getEmail(message.getSession());
                    log.info("New Email : {}", correctEmail);
                    String response2 = googleOperationsService.resetUserPassword(correctEmail);

                    GoogleRequestUserDto user = googleOperationsService.getUserInfo(email.getAnswer());
                    String phoneNumber = user.getPhoneNumber();

                    smsService.sendMessage(phoneNumber,response2);

                    commonPool.removeResponse(message.getSession());
                    commonPool.addResponse(message.getSession(), response2);

                    log.info("Response2 : {}", response2);

                    sendQuestion(message.getSession(), response2, TEXT);
                    clearSessionWithSayThanks(message.getSession(), TEXT);

                    log.error("No valid email found for password reset");
                    sendQuestion(message.getSession(), "Error: No valid email found", TEXT);
                    clearSessionWithSayThanks(message.getSession(), TEXT);
                }

            } else {
                sessionIndex.set(2);
                log.error("OTP not verified!");

                commonPool.getUserResponses().get(message.getSession()).remove("inputOTP"); // Alternative removal approach

                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
            }


        }

        if(index == 2) {
            log.info("Removed OTP {}", commonPool.getOTP(message.getSession()));
            String newOTP = message.getMessage().trim();
            commonPool.addInputOTP(message.getSession(), newOTP);
            String correctOTP = commonPool.getInputOTP(message.getSession());
            log.info("Corrected OTP : {}", correctOTP);
            String givenOTP = commonPool.getOTP(message.getSession());
            log.info("Generated OTP : {}", givenOTP);

            long otpTimestamp = commonPool.getOTPTimestamp(message.getSession());
            long currentTime = System.currentTimeMillis();

            if (currentTime - otpTimestamp > OTP_EXPIRATION_TIME) {
                // OTP has expired
                log.info("OTP expired for session: {}", message.getSession());
                sessionIndex.set(11); // New question index for OTP expiration
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                String body = "Your password reset attempt failed. Session expired.";
                String to = commonPool.getEmail(message.getSession());
                confirmationEmail(emailSender, to, body);
                clearSessionWithSayThanks(message.getSession(), TEXT);
                return;
            }

            if (givenOTP.equals(correctOTP)) {
                log.info("OTP verified for the second time!");
                sessionIndex.set(3);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());

                Question email = commonPool.getAnswerForQuestion(message.getSession(), "0");

                if (email != null) {
                    String response = googleOperationsService.resetUserPassword(email.getAnswer());
                    log.info("Response : {}", response);

                    GoogleRequestUserDto user = googleOperationsService.getUserInfo(email.getAnswer());
                    String phoneNumber = user.getPhoneNumber();
                    smsService.sendMessage(phoneNumber, response);

                    commonPool.removeResponse(message.getSession());
                    commonPool.addResponse(message.getSession(), response);

                    sessionIndex.set(9);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());

                    sessionIndex.set(12);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());

                    sessionIndex.set(10);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());

                } else {

                    String correctEmail = commonPool.getEmail(message.getSession());
                    log.info("New Email : {}", correctEmail);
                    String response = googleOperationsService.resetUserPassword(correctEmail);
                    log.info("Response : {}", response);

                    GoogleRequestUserDto user = googleOperationsService.getUserInfo(email.getAnswer());
                    String phoneNumber = user.getPhoneNumber();

                    smsService.sendMessage(phoneNumber,response);

                    commonPool.removeResponse(message.getSession());
                    commonPool.addResponse(message.getSession(), response);

                    sendQuestion(message.getSession(), response, TEXT);
                    clearSessionWithSayThanks(message.getSession(), TEXT);

                    log.error("No valid email found for password reset");
                    sendQuestion(message.getSession(), "Error: No valid email found", TEXT);
                    clearSessionWithSayThanks(message.getSession(), TEXT);
                }
            } else {
                log.info("ERROR");
                sessionIndex.set(8);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                String body = "Your password reset attempt failed. Incorrect OTP.";
                String to = commonPool.getEmail(message.getSession());
                confirmationEmail(emailSender, to, body);
                clearSessionWithSayThanks(message.getSession(), TEXT);
            }
        }

        if(index == 10) {
            String confirmationAnswer = message.getMessage().trim().toLowerCase();
            if(confirmationAnswer.equals("yes")) {
                String response = commonPool.getResponse(message.getSession());
                log.info("THE FINAL RESPONSE : {}", response);
                sendQuestion(message.getSession(), response, TEXT);
                String body = "Your password reset attempt was successful. Your password has been sent to your registered mobile number.";
                String to = commonPool.getEmail(message.getSession());
                confirmationEmail(emailSender, to, body);
                clearSessionWithSayThanks(message.getSession(), TEXT);
            }else {
                String body = "Your password reset attempt was successful. Your password has been sent to your registered mobile number.";
                String to = commonPool.getEmail(message.getSession());
                confirmationEmail(emailSender, to, body);
                clearSessionWithSayThanks(message.getSession(), TEXT);
            }
        }


    }

    private void clearSessionWithSayThanks(String session, String type) {
        Message questionMessage = new Message(session, Dictionary.AI, "Thank you...if you need to any assets say 'hi'", new Date().getTime(), type);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        commonPool.removeSessionData(session);
    }

    private void sendQuestion(String session, String question, String type) {
        Message questionMessage = new Message(session, Dictionary.AI, question, new Date().getTime(), type);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        log.info("Sent question to {}: {}", session, question);
    }

    private void confirmationEmail(String from, String to, String body) {
        String confirmationAnswer = emailService.sendAnotherEmail(to, body);
    }
}
