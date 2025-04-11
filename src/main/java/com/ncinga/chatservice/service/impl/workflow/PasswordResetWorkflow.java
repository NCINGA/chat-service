package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.GoogleRequestUserDto;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.Question;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
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
        // commonPool.addQuestionWithAnswer(message.getSession(), String.valueOf(index), questions.get(index).getQuestion(), message.getMessage());


        if(index == 0) {
            String email =  message.getMessage();
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
                log.info("Parana email eka : {}", commonPool.getEmail(message.getSession()));
                commonPool.addEmail(message.getSession(), correctEmail);
                String newEmail = commonPool.getEmail(message.getSession());
                log.info("Email : {}", newEmail);


                /*
                commonPool.getUserResponses().putIfAbsent(message.getSession(), new HashMap<>());
                commonPool.getUserResponses().get(message.getSession()).put(questions.get(0).getQuestion(), email);
                commonPool.addQuestionWithAnswer(message.getSession(), "0", questions.get(0).getQuestion(), email);
                */
            } else {
                if (user.getStatus() != null && user.getStatus().equals(ACCOUNT_SUSPENDED)) {
                    sessionIndex.set(-2);

                    WorkFlowQuestion SecondQuestion = questions.get(questions.size() - 2);
                    sendQuestion(message.getSession(), SecondQuestion.getQuestion(), SecondQuestion.getInputType());

                    WorkFlowQuestion firstQuestion = questions.get(questions.size() - 1);
                    sendQuestion(message.getSession(), firstQuestion.getQuestion(), firstQuestion.getInputType());
                    // commonPool.getSessionIndices().remove(message.getSession());

                    return;
                }

                String number = user.getPhoneNumber();
                log.info("Phone number : {}", number);
                String otp = smsService.send(number);
                log.info("OTP : {}", otp);
                commonPool.addOTP(message.getSession(), otp);

                sessionIndex.incrementAndGet();
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                commonPool.addQuestionWithAnswer(message.getSession(), String.valueOf(index), questions.get(index).getQuestion(), message.getMessage());
            }
        }

        if(index == 1){
            String enteredOTP = message.getMessage();
            commonPool.addInputOTP(message.getSession(), enteredOTP);
            String inputOTP = commonPool.getInputOTP(message.getSession());
            log.info("Input OTP : {}", inputOTP);

            String generatedOTP = commonPool.getOTP(message.getSession());
            log.info("Generated OTP : {}", generatedOTP);

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
                    sendQuestion(message.getSession(), response, TEXT);
                    clearSessionWithSayThanks(message.getSession(), TEXT);

                } else {

                    String correctEmail = commonPool.getEmail(message.getSession());
                    log.info("Aluth Email : {}", correctEmail);
                    String response = googleOperationsService.resetUserPassword(correctEmail);
                    log.info("Response : {}", response);

                    smsService.send(response);
                    sendQuestion(message.getSession(), response, TEXT);
                    clearSessionWithSayThanks(message.getSession(), TEXT);

                    log.error("No valid email found for password reset");
                    sendQuestion(message.getSession(), "Error: No valid email found", TEXT);
                    clearSessionWithSayThanks(message.getSession(), TEXT);
                }

            } else {
                sessionIndex.set(2);
                log.error("OTP not verified!");

                // Fix 1: Clear the input OTP properly before requesting a new one
                commonPool.getUserResponses().get(message.getSession()).remove("inputOTP"); // Alternative removal approach
                // Or implement a more direct approach in the CommonPool class

                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
            }


        }

        if(index == 2) {
            log.info("Removed OTP {}", commonPool.getOTP(message.getSession()));
            String newOTP = message.getMessage();
            commonPool.addInputOTP(message.getSession(), newOTP);
            String correctOTP = commonPool.getInputOTP(message.getSession());
            log.info("Corrected OTP : {}", correctOTP);
            String givenOTP = commonPool.getOTP(message.getSession());
            log.info("Generated OTP : {}", givenOTP);

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
                    sendQuestion(message.getSession(), response, TEXT);
                    clearSessionWithSayThanks(message.getSession(), TEXT);

                } else {

                    String correctEmail = commonPool.getEmail(message.getSession());
                    log.info("Aluth Email : {}", correctEmail);
                    String response = googleOperationsService.resetUserPassword(correctEmail);
                    log.info("Response : {}", response);

                    smsService.send(response);
                    sendQuestion(message.getSession(), response, TEXT);
                    clearSessionWithSayThanks(message.getSession(), TEXT);

                    log.error("No valid email found for password reset");
                    sendQuestion(message.getSession(), "Error: No valid email found", TEXT);
                    clearSessionWithSayThanks(message.getSession(), TEXT);
                }
            } else {
                log.info("Waradii!");
                sessionIndex.set(8);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
                clearSessionWithSayThanks(message.getSession(), TEXT);
            }
        }

//        if (index == 3){
//            Question email = commonPool.getAnswerForQuestion(message.getSession(), questions.get(0).getQuestion());
//
//            String response = googleOperationsService.resetUserPassword(email.getAnswer());
//            log.info("Response : {}", response);
//
//            sendQuestion(message.getSession(), response, TEXT);
//            clearSessionWithSayThanks(message.getSession(), TEXT);
//
//            commonPool.removeSessionData(message.getSession());
//
//        }



//        String email1 = commonPool.getUserData(message.getSession(), "email");
//        // Verify OTP (mock implementation)
//        boolean isOtpValid = otpService.validateOtp(email1, enteredOtp);
//
//        if (isOtpValid) {
//            // OTP valid, move to confirmation question
//            sessionIndex.set(3); // Move to "Are you sure" question
//            nextQuestion = questions.get(sessionIndex.get());
//            sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
//        } else {
//            // OTP invalid, send error message and retry
//            sessionIndex.set(2); // Set to invalid OTP message
//            nextQuestion = questions.get(sessionIndex.get());
//            sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
//
//            // Move back to OTP entry
//            sessionIndex.set(1);
//            nextQuestion = questions.get(sessionIndex.get());
//            sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType());
//        }

    }

    private void clearSession(String session) {
        commonPool.removeSessionData(session);
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
}
