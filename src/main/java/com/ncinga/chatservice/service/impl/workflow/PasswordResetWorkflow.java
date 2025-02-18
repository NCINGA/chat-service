package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.AzureUserDto;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.Question;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.*;
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
   private final UserService userService;



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
                sessionIndex.set(8);
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
            Question email = commonPool.getAnswerForQuestion(message.getSession(), "1");
            if (confirmation.getAnswer().equals("yes")) {
                boolean isExist = getUserByEmailService.doesUserExist(email.getAnswer());
                log.info("Email already exist {}", isExist);
                if (!isExist) {
                    sessionIndex.set(7);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                    sessionIndex.set(1);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                    commonPool.removeQuestion(message.getSession(), "1");
                    commonPool.removeQuestion(message.getSession(), "2");
                } else {
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                    AzureUserDto userDetail = getUserByEmailService.getUserByEmail(email.getAnswer());
                    log.info("User details {}", userDetail);
                    String OTP = otpGenerateService.generateOTP();
                    smsService.send(userDetail.getMobilePhone(), OTP);
                    commonPool.addOTP(message.getSession(), OTP);
                }
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

        if (sessionIndex.get() == 8) {
            nextQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());

        }
        if (sessionIndex.get() == 9) {
            nextQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());

        }

        if (sessionIndex.get() == 10) {
            Question username = commonPool.getAnswerForQuestion(message.getSession(), "8");
            Question password = commonPool.getAnswerForQuestion(message.getSession(), "9");
            log.info("admin username {}, password {}", username.getAnswer(), password.getAnswer());
            boolean user = userService.findByRole(username.getAnswer(), password.getAnswer(), "ADMIN");
            log.info("admin user {}", user);
            if (user) {
                log.info("authenticate success...");
                sessionIndex.set(11);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
            } else {
                log.info("authenticate failed...");
                sessionIndex.set(10);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                sessionIndex.set(8);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                commonPool.removeQuestion(message.getSession(), "8");
                commonPool.removeQuestion(message.getSession(), "9");
            }
        }


        if (sessionIndex.get() == 12) {
            nextQuestion = questions.get(sessionIndex.get());
            Question email = commonPool.getAnswerForQuestion(message.getSession(), "11");
            sendQuestion(message.getSession(), nextQuestion.getQuestion() + email.getAnswer(), nextQuestion.getInputType(), nextQuestion.getArgs());
        }

        if (sessionIndex.get() == 13) {
            Question confirmation = commonPool.getAnswerForQuestion(message.getSession(), "12");
            if (confirmation.getAnswer().equals("yes")) {
                Question email = commonPool.getAnswerForQuestion(message.getSession(), "11");
                boolean isExist = getUserByEmailService.doesUserExist(email.getAnswer());
                log.info("Email already exist {}", isExist);
                if (!isExist) {
                    sessionIndex.set(13);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                    sessionIndex.set(11);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion().replace("Your identity has been verified. ", ""), nextQuestion.getInputType(), nextQuestion.getArgs());
                    commonPool.removeQuestion(message.getSession(), "11");
                } else {
                    sessionIndex.set(14);
                    nextQuestion = questions.get(sessionIndex.get());
                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                    AzureUserDto userDetail = getUserByEmailService.getUserByEmail(email.getAnswer());
                    log.info("User details {}", userDetail);
                    String OTP = otpGenerateService.generateOTP();
                    smsService.send(userDetail.getMobilePhone(), OTP);
                    commonPool.addOTP(message.getSession(), OTP);
                }
            } else {
                sessionIndex.set(11);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion().replace("Your identity has been verified. ", ""), nextQuestion.getInputType(), nextQuestion.getArgs());
                commonPool.removeQuestion(message.getSession(), "11");
                commonPool.removeQuestion(message.getSession(), "12");
            }

        }
        if (sessionIndex.get() == 15) {
            Question inputOTP = commonPool.getAnswerForQuestion(message.getSession(), "14");
            log.info("User given otp {}", inputOTP.getAnswer());
            String generatedOTP = commonPool.getOTP(message.getSession());
            if (generatedOTP.equals(inputOTP.getAnswer())) {
                log.info("OTP verified...");
                commonPool.removeOTP(message.getSession());
                sessionIndex.set(16);
            } else {
                sessionIndex.set(14);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion().replace("An OTP has been sent to your registered mobile number. Please enter the 4-digit OTP to verify your identity.", "OTP is wrong please check and retry.."), nextQuestion.getInputType(), nextQuestion.getArgs());
                commonPool.removeQuestion(message.getSession(), "14");
                log.info("OTP not verified...");
            }
        }

        if (sessionIndex.get() == 16) {
            Question email = commonPool.getAnswerForQuestion(message.getSession(), "11");
            nextQuestion = questions.get(sessionIndex.get());
            String newPassword = passwordResetService.resetPassword(email.getAnswer());
            sendQuestion(message.getSession(), nextQuestion.getQuestion() + newPassword, nextQuestion.getInputType(), nextQuestion.getArgs());
            sessionIndex.set(17);
            Thread.sleep(1000);
            nextQuestion = questions.get(sessionIndex.get());
            sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
        }

        if (sessionIndex.get() == 18) {
            Question reset = commonPool.getAnswerForQuestion(message.getSession(), "17");
            if (reset.getAnswer().equals("yes")) {
                commonPool.removeOTP(message.getSession());
                commonPool.removeUserResponseData(message.getSession());
                commonPool.removeOTP(message.getSession());
                sessionIndex.set(0);
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
