package com.ncinga.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class EmailRequestDto {
    private Map<String, String> sender;
    private List<Map<String, String>> to;
    private List<Map<String, String>> cc;
    private String subject;
    private String textContent;

    // Getters and setters
}