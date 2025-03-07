package com.ncinga.chatservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private boolean accountEnabled;
    private String displayName;
    private String mailNickname;
    private String userPrincipalName;
    private String mobilePhone;

    @JsonProperty("passwordProfile")
    private Map<String, Object> passwordProfile;

}
