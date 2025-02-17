package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.config.ChatSinkManager;
import com.ncinga.chatservice.dto.Message;
import com.ncinga.chatservice.dto.Question;
import com.ncinga.chatservice.dto.WorkFlowQuestion;
import com.ncinga.chatservice.service.PasswordResetService;
import com.ncinga.chatservice.service.SMSService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetWorkflow implements WorkflowProcess {
    private static final String OTP_NUMBER = "7070";
    private final ChatSinkManager<Message> chatSinkManager;
    private final CommonPool commonPool;
    private final List<WorkFlowQuestion> questions;
    private final PasswordResetService passwordResetService;
    private final SMSService smsService;


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
            } else {
                sessionIndex.set(1);
                nextQuestion = questions.get(sessionIndex.get());
                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
                commonPool.removeQuestion(message.getSession(), "1");
                commonPool.removeQuestion(message.getSession(), "2");
            }
        }


    }


//    @Override
//    public void execute(AtomicInteger sessionIndex, Message message) throws InterruptedException {
//        int index = sessionIndex.get();
//        WorkFlowQuestion nextQuestion;
//        if (index >= questions.size() || index == -1) {
//            log.info("All questions answered or session not started for user: {}", message.getSession());
//            return;
//        }
//
//        log.info("message : {}", message);
//        commonPool.getUserResponses().putIfAbsent(message.getSession(), new HashMap<>());
//        commonPool.getUserResponses().get(message.getSession()).put(questions.get(index).getQuestion(), message.getMessage());
//        commonPool.addQuestionWithAnswer(message.getSession(), String.valueOf(index), questions.get(index).getQuestion(), message.getMessage());
//
//        if (index + 1 < questions.size()) {
//            sessionIndex.incrementAndGet();
//            if (message.getMessage().equalsIgnoreCase("yes")) {
//                //admin flow
//                //sessionIndex.addAndGet(1);
//                sessionIndex.set(10);
//            } else if (message.getMessage().equalsIgnoreCase("no")) {
//                clearSession(message.getSession());
//            }
//
//            //update role base scenario
//            if (sessionIndex.get() == 10) {
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//
//            }
//
//            if (sessionIndex.get() == 11) {
//                Question role = commonPool.getAnswerForQuestion(message.getSession(), "10");
//                log.info("Selected role is {}", role.getAnswer());
//                sessionIndex.set(12);
////                sendQuestion(message.getSession(), nextQuestion.getQuestion() + role.getAnswer(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                if (role.getAnswer().equals("user")) {
////                    Thread.sleep(1000);
////                    nextQuestion = questions.get(sessionIndex.get());
////                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                    sessionIndex.set(13);
//                    nextQuestion = questions.get(sessionIndex.get());
//                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                } else {
//                    sessionIndex.set(22);
//                    log.info("session index {}", sessionIndex);
//                    nextQuestion = questions.get(sessionIndex.get());
//                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//
//                }
//            }
//
//            if(sessionIndex.get() == 23){
//                String generatedPassword = "test@123";
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion() + generatedPassword, nextQuestion.getInputType(), nextQuestion.getArgs());
//                sessionIndex.set(24);
//            }
//
//            if(sessionIndex.get() == 24){
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                clearSessionWithSayThanks(message.getSession(), TEXT);
//            }
//
//            if (sessionIndex.get() == 14) {
//                Question mobileNumber = commonPool.getAnswerForQuestion(message.getSession(), "13");
//                nextQuestion = questions.get(sessionIndex.get());
//                sessionIndex.set(15);
//                String number = mobileNumber.getAnswer();
//                log.info("Given mobile number {}", number);
//                smsService.send(mobileNumber.getAnswer(), OTP_NUMBER);
//                sendQuestion(message.getSession(), nextQuestion.getQuestion() + mobileNumber.getAnswer(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                Thread.sleep(1000);
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                sessionIndex.set(16);
//                Thread.sleep(1000);
//            }
//
//
//            if (sessionIndex.get() == 16) {
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//            }
//
//            if (sessionIndex.get() == 17) {
//                Question otp = commonPool.getAnswerForQuestion(message.getSession(), "16");
//                if (otp.getAnswer().equals(OTP_NUMBER)) {
//                    log.info("OTP verify success");
//                    nextQuestion = questions.get(sessionIndex.get());
//                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                    sessionIndex.set(19);
//
//                } else {
//                    sessionIndex.set(18);
//                    log.info("OTP verification failed");
//                    nextQuestion = questions.get(sessionIndex.get());
//                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                    Thread.sleep(1000);
//                    sessionIndex.set(16);
//                    nextQuestion = questions.get(sessionIndex.get());
//                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                    commonPool.removeQuestion(message.getSession(), "16");
//                }
//
//            }
//
//            if (sessionIndex.get() == 19) {
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//            }
//
//            if (sessionIndex.get() == 20) {
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//            }
//
//            if (sessionIndex.get() == 21) {
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                Question username = commonPool.getAnswerForQuestion(message.getSession(), "19");
//                Question password = commonPool.getAnswerForQuestion(message.getSession(), "20");
//                log.info("user name and password is {} {}", username.getAnswer(), password.getAnswer());
//                String response = passwordResetService.resetPassword(username.getAnswer(), password.getAnswer());
//                sendQuestion(message.getSession(), response, TEXT, null);
//                clearSessionWithSayThanks(message.getSession(), TEXT);
//            }
//
//            log.info("sessionIndex {}", sessionIndex);
/////////////////////////// end admin flow
//            if (sessionIndex.get() == 2) {
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//            }
//
//            if (sessionIndex.get() == 3) {
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//            }
//
//            if (sessionIndex.get() == 4) {
//                sessionIndex.set(6);
//                nextQuestion = questions.get(sessionIndex.get());
//                sessionIndex.set(4);
//                Question username = commonPool.getAnswerForQuestion(message.getSession(), "2");
//                Question password = commonPool.getAnswerForQuestion(message.getSession(), "3");
//                log.info("user name and password {} ,{}", username.getAnswer(), password.getAnswer());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                if (commonPool.isAuthSuccess() == false && (username.getAnswer().equals(logUser) && password.getAnswer().equals(logPassword))) {
//                    log.info("Auth Success..");
//                    commonPool.setAuth(true);
//                    sessionIndex.set(7);
//                    nextQuestion = questions.get(sessionIndex.get());
//                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                    sessionIndex.set(4);
//                } else {
//                    log.info("Auth failed..");
//                    sessionIndex.set(8);
//                    nextQuestion = questions.get(sessionIndex.get());
//                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                    sessionIndex.set(2);
//                    nextQuestion = questions.get(sessionIndex.get());
//                    sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//                    commonPool.removeUserResponseData(message.getSession());
//                }
//            }
//
//            if (sessionIndex.get() == 4) {
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//            }
//
//            if (sessionIndex.get() == 5) {
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), nextQuestion.getQuestion(), nextQuestion.getInputType(), nextQuestion.getArgs());
//            }
//
//            if (sessionIndex.get() == 6) {
//                Question username = commonPool.getAnswerForQuestion(message.getSession(), "4");
//                Question password = commonPool.getAnswerForQuestion(message.getSession(), "5");
//                log.info("user name and password {} ,{}", username.getAnswer(), password.getAnswer());
//                String response = passwordResetService.resetPassword(username.getAnswer(), password.getAnswer());
//
//                sessionIndex.set(9);
//                nextQuestion = questions.get(sessionIndex.get());
//                sendQuestion(message.getSession(), response, TEXT, null);
//                clearSessionWithSayThanks(message.getSession(), TEXT);
//              //  sessionIndex.set(10);
//            }
//
////            if (sessionIndex.get() == 10) {
////                nextQuestion = questions.get(sessionIndex.get());
////                sendQuestion(message.getSession(), nextQuestion.getQuestion(), message.getInputType());
////                clearSessionWithSayThanks(message.getSession(), TEXT);
////            }
//
//        }
//    }

    private void clearSession(String session) {
        commonPool.removeSessionData(session);
    }

    private void clearSessionWithSayThanks(String session, String type) {
        Message questionMessage = new Message(session, Dictionary.AI, "Thank you...if you need to any assets say 'hi'", new Date().getTime(), type, null);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        commonPool.removeSessionData(session);
    }

    private void sendQuestion(String session, String question, String type, Object args) {
        Message questionMessage = new Message(session, Dictionary.AI, question, new Date().getTime(), type, args);
        chatSinkManager.getChatSink().get(session).tryEmitNext(questionMessage);
        log.info("Sent question to {}: {}", session, question);
    }
}
