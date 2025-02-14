package com.ncinga.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Question {
    private String questionId;
    private String question;
    private String answer;
    public Question() {
    }
}
