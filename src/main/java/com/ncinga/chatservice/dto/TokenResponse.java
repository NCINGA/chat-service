package com.ncinga.chatservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
}
