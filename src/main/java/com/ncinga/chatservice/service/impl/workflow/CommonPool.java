package com.ncinga.chatservice.service.impl.workflow;

import com.ncinga.chatservice.dto.Question;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Slf4j
@Service
public class CommonPool {

    private final Map<String, Map<String, List<Question>>> questionPool = new HashMap<>();
    private final Map<String, AtomicInteger> userIndices = new HashMap<>();
    private final Map<String, Map<String, String>> userResponses = new HashMap<>();


    public AtomicInteger getUserIndex(String user) {
        return userIndices.computeIfAbsent(user, k -> new AtomicInteger(-1));
    }

    public void addQuestionWithAnswer(String user, String questionId, String question, String answer) {
        log.info("Add question for user '{}': questionId = '{}' question = '{}', answer = '{}'", user, questionId, question, answer);
        Map<String, List<Question>> userQuestions = questionPool.computeIfAbsent(user, k -> new HashMap<>());
        List<Question> questions = userQuestions.computeIfAbsent(questionId, k -> new ArrayList<>());
        questions.add(new Question(questionId, question, answer));
    }

    public Question getAnswerForQuestion(String user, String questionId) {
        log.info("Attempting to retrieve answer for user: '{}' and questionId: '{}'", user, questionId);

        Map<String, List<Question>> userQuestions = questionPool.get(user);

        if (userQuestions != null) {
            log.info("Found questions for user: '{}'", user);
            List<Question> questions = userQuestions.get(questionId);

            if (questions != null && !questions.isEmpty()) {
                return questions.get(0);
            } else {
                log.warn("No question found with questionId: '{}' for user: '{}'", questionId, user);
            }
        } else {
            log.warn("No questions found for user: '{}'", user);
        }
        log.info("Returning null for user: '{}' and questionId: '{}'", user, questionId);
        return null;
    }
}
