package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.service.impl.workflow.IntentWorkflow;
import com.ncinga.chatservice.service.impl.workflow.IntentFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkflowProcessImpl {
    private static final String INTENT = "CHANGE_PASSWORD";



    public Object test() throws IllegalAccessException {
        IntentWorkflow intentWorkflow = IntentFactory.getIntent("kk");
//        Map<String, Object> questions = intentWorkflow.getQuestions();
//        Map<String, Object> questionsWithAnswers = intentWorkflow.getQuestions();

//        questions.forEach((question, value) -> {
//            log.info("Question {} Value {} ", question, value);
//
//        });

        return "";
    }
}
