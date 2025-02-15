package com.ncinga.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkFlowQuestion {
    private String question;
    private RequiredTypes requiredType;
    private InputTypes inputType;
    private int whenTrueQuestionId;
    private int whenFalseQuestionId;
}
