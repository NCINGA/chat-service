package com.ncinga.chatservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaqRequest {
    private String q;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("collection_name")
    private String collectionName;

    private Integer k;
}
