package com.ncinga.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String user;
    private String message;
    private long timestamp;
    private String inputType;
}
