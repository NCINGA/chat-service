package com.ncinga.chatservice.service.impl;

import com.ncinga.chatservice.service.WorkflowProcess;
import com.ncinga.chatservice.service.impl.workflow.Workflow;
import com.ncinga.chatservice.service.impl.workflow.WorkflowFactory;
import com.ncinga.chatservice.service.impl.workflow.Workflows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class WorkflowProcessImpl implements WorkflowProcess {
    private static final String INTENT = "CHANGE_PASSWORD";


    @Override
    public Object test() throws IllegalAccessException {
        Workflow workflow = WorkflowFactory.getWorkflow(Workflows.RESET_PASSWORD);
//        Map<String, Object> questions = workflow.getQuestions();
//        Map<String, Object> questionsWithAnswers = workflow.getQuestions();

//        questions.forEach((question, value) -> {
//            log.info("Question {} Value {} ", question, value);
//
//        });

        return "";
    }
}
