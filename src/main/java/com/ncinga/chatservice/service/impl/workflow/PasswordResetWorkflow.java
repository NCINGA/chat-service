package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.AzureUserDto;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.Question;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.GetUserByEmailService;
import com.ncinga.chatservice.service.OTPGenerateService;
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
    private static final String OTP_NUMBER = "7070";
    private final ChatSinkManager<Message> chatSinkManager;
    private final CommonPool commonPool;
    private final List<WorkFlowQuestion> questions;
    private final PasswordResetService passwordResetService;
    private final GetUserByEmailService getUserByEmailService;
    private final SMSService smsService;
    private final OTPGenerateService otpGenerateService;


    private String logUser = "shehan";
    private String logPassword = "12345";


    @Override
    public void execute(AtomicInteger sessionIndex, Message message) throws InterruptedException {
        int index = sessionIndex.get();
        WorkFlowQuestion nextQuestion;
        if (index >= questions.size() || index == -1) {
            log.info("All questions answered or session not started for user: {}", message.getSession());
            return;
        }

        log.info("message : {}", message);
        commonPool.getUserResponses().putIfAbsent(message.getSession(), new HashMap<>());
        commonPool.getUserResponses().get(message.getSession()).put(questions.get(index).getQuestion(), message.getMessage());
        commonPool.addQuestionWithAnswer(message.getSession(), String.valueOf(index), questions.get(index).getQuestion(), message.getMessage());


        if (index + 1 < questions.size()) {
            sessionIndex.incrementAndGet();
            if (message.getMessage().equalsIgnoreCase("admin")) {

            } else if (message.getMessage().equalsIgnoreCase("user")) {
                sessionIndex.set(1);
            }
        }

        if (sessionIndex.get() == 1) {
            nextQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
        }


        if (sessionIndex.get() == 2) {
            Question email = commonPool.getAnswerForQuestion(message.getSession(), "1");
            nextQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), nextQuestion.getQuestion()+email.getAnswer(), nextQuestion.getInputType(), nextQuestion.getArgs());
        }

        if (sessionIndex.get() == 3) {
            Question confirmation = commonPool.getAnswerForQuestion(message.getSession(), "2");
            if (confirmation.getAnswer().equals("yes")) {
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                Question email = commonPool.getAnswerForQuestion(message.getSession(), "1");
                AzureUserDto userDetail = getUserByEmailService.getUserByEmail(email.getAnswer());
                log.info("User details {}", userDetail);
                String OTP = otpGenerateService.generateOTP();
                smsService.send(userDetail.getMobilePhone(), OTP);
                commonPool.addOTP(message.getSession(), OTP);
            } else {
                sessionIndex.set(1);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                commonPool.removeQuestion(message.getSession(), "1");
                commonPool.removeQuestion(message.getSession(), "2");
            }
        }
        if (sessionIndex.get() == 4) {
            Question inputOTP = commonPool.getAnswerForQuestion(message.getSession(), "3");
            log.info("User given otp {}", inputOTP.getAnswer());
            String generatedOTP = commonPool.getOTP(message.getSession());
            if (generatedOTP.equals(inputOTP.getAnswer())) {
                log.info("OTP verified...");
                commonPool.removeOTP(message.getSession());
                sessionIndex.set(5);
            } else {
                sessionIndex.set(3);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                commonPool.removeQuestion(message.getSession(), "3");
                log.info("OTP not verified...");
            }
        }

        if (sessionIndex.get() == 5) {
            Question email = commonPool.getAnswerForQuestion(message.getSession(), "1");
            log.info("Get id by email {}", email.getAnswer());
            AzureUserDto userDto = getUserByEmailService.getUserByEmail(email.getAnswer());
            log.info("User details {}", userDto);
            String newPassword = passwordResetService.resetPassword(userDto.getId());
            nextQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), nextQuestion.getQuestion() + newPassword, nextQuestion.getInputType(), nextQuestion.getArgs());
            sessionIndex.set(6);
            Thread.sleep(1000);
            nextQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
        }

        if (sessionIndex.get() == 7) {
            Question reset = commonPool.getAnswerForQuestion(message.getSession(), "6");
            if (reset.getAnswer().equals("yes")) {
                commonPool.removeOTP(message.getSession());
                commonPool.removeUserResponseData(message.getSession());
                commonPool.removeOTP(message.getSession());
                sessionIndex.set(1);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
            } else {
                clearSessionWithSayThanks(message.getSession(), TEXT);
            }
        }

    }


    private void clearSession(String session) {
        commonPool.removeSessionData(session);
    }

    private void clearSessionWithSayThanks(String session, String type) {
        Message questionMessage = new Message(session, Dictionary.AI, "Thank you for using the Password Reset Assistant. If you need further assistance, please contact IT support", new Date().getTime(), type, null);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        commonPool.removeSessionData(session);
    }

    private void sendQuestion(String session, String question, String type, Object args) {
        Message questionMessage = new Message(session, Dictionary.AI, question, new Date().getTime(), type, args);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        log.info("Sent question to {}: {}", session, question);
    }
}
