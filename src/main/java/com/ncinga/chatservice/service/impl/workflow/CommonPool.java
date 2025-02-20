package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Question;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
@Data
@Slf4j
@Service
public class CommonPool {

    private final Map<String, Map<String, List<Question>>> questionPool = new HashMap<>();
    private final Map<String, AtomicInteger> sessionIndices = new HashMap<>();
    private final Map<String, Map<String, String>> userResponses = new HashMap<>();
    private final Map<String, String> userOTPPool = new HashMap<>();
    private boolean authSuccess = false;


    public void setAuth(boolean auth) {
        authSuccess = auth;
    }

    public void addOTP(String session, String otp) {
        log.info("Adding OTP for session '{}': otp = '{}'", session, otp);
        userOTPPool.put(session, otp);
    }

    public String getOTP(String session) {
        String otp = userOTPPool.get(session);
        log.info("session id {}, OTP {}", session, otp);
        return otp;
    }

    public void removeOTP(String session) {
        if (userOTPPool.containsKey(session)) {
            userOTPPool.remove(session);
            log.info("Removed OTP for session '{}'", session);
        }
    }


    public AtomicInteger getSessionIndex(String session) {
        return sessionIndices.computeIfAbsent(session, k -> new AtomicInteger(-1));
    }

    public void addQuestionWithAnswer(String session, String questionId, String question, String answer) {
        log.info("Add question for user '{}': questionId = '{}' question = '{}', answer = '{}'", session, questionId, question, answer);
        Map<String, List<Question>> userQuestions = questionPool.computeIfAbsent(session, k -> new HashMap<>());
        List<Question> questions = userQuestions.computeIfAbsent(questionId, k -> new ArrayList<>());
        questions.add(new Question(questionId, question, answer));
    }

    public void removeQuestion(String session, String questionId) {
        log.info("Removing question for user '{}': questionId = '{}'", session, questionId);
        Optional.ofNullable(questionPool.get(session))
                .ifPresent(userQuestions -> {
                    userQuestions.remove(questionId);
                    if (userQuestions.isEmpty()) {
                        questionPool.remove(session);
                    }
                });
        log.info("Updated question pool for session '{}': {}", session, questionPool.get(session));
    }


    public boolean hasQuestionBeenAsked(String session, String question) {
        log.info("Checking if question '{}' has been asked for session '{}'", question, session);
        Map<String, List<Question>> userQuestions = questionPool.get(session);
        if (userQuestions == null) {
            log.info("No previous questions found for session '{}'", session);
            return false;
        }
        for (List<Question> questions : userQuestions.values()) {
            for (Question q : questions) {
                if (q.getQuestion().equalsIgnoreCase(question)) {
                    log.info("Question '{}' has already been asked for session '{}'", question, session);
                    return true;
                }
            }
        }
        log.info("Question '{}' has NOT been asked for session '{}'", question, session);
        return false;
    }

    public Question getAnswerForQuestion(String session, String questionId) {
        log.info("Attempting to retrieve answer for user: '{}' and questionId: '{}'", session, questionId);
        Map<String, List<Question>> userQuestions = questionPool.get(session);

        if (userQuestions != null) {
            log.info("Found questions for user: '{}'", session);
            List<Question> questions = userQuestions.get(questionId);

            if (questions != null && !questions.isEmpty()) {
                return questions.get(0);
            } else {
                log.warn("No question found with questionId: '{}' for user: '{}'", questionId, session);
            }
        } else {
            log.warn("No questions found for user: '{}'", session);
        }
        log.info("Returning null for user: '{}' and questionId: '{}'", session, questionId);
        return null;
    }

    public void removeUserResponseData(String session) {
        if (questionPool.containsKey(session)) {
            questionPool.remove(session);
            log.info("Removed question data for session: {}", session);
        }
    }

    public void removeSessionData(String session) {
        if (questionPool.containsKey(session)) {
            questionPool.remove(session);
            log.info("Removed question data for session: {}", session);
        }
//        if (sessionIndices.containsKey(session)) {
//            sessionIndices.remove(session);
//            log.info("Removed session index for session: {}", session);
//        }
        if (userResponses.containsKey(session)) {
            userResponses.remove(session);
            log.info("Removed user responses for session: {}", session);
        }
        authSuccess = false;
    }

    public void clearAllSessions() {
        questionPool.clear();
        sessionIndices.clear();
        userResponses.clear();
        log.info("All session-related data cleared.");
    }
}
