package com.ncinga.chatservice.controllers;

import com.ncinga.chatservice.service.impl.workflow.CommonPool;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/workflow")
public class WorkflowController {

    private final CommonPool pool;

    @GetMapping(path = "/test")
    public Object test() throws IllegalAccessException {
        pool.addQuestionWithAnswer("user1", "q1", "What is Java?", "A programming language.");
        pool.addQuestionWithAnswer("user1", "q2", "What is Spring?", "A framework for Java.");


        return "";
    }
}
