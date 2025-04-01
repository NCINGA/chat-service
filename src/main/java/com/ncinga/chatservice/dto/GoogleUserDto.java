package com.ncinga.chatservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleUserDto {

    private String primaryEmail;
    @JsonProperty("name")
    private Map<String, Object> name;
    private String password;
    private Boolean changePasswordAtNextLogin;
    private String orgUnitPath;
    private boolean isAdmin;
    @JsonProperty("Phone")
    private Map<String, Object> Phone;
}
