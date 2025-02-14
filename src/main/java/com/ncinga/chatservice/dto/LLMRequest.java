package com.ncinga.chatservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LLMRequest {
    @JsonProperty("session_id")
    private String user;
    @JsonProperty("user_query")
    private String query;

}
