package com.ncinga.chatservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LLMRequest {
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("user_query")
    private String query;
    private String email;

}
