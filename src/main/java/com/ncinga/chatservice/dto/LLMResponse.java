package com.ncinga.chatservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LLMResponse {
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("use_query")
    private String useQuery;
    private String intent;
    private String detail;
    private String response;
    @JsonProperty("chat_termination")
    private boolean termination;
}
