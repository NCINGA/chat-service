package com.ncinga.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthenticateDto {

    private String username;
    private String password;
    private List<String> roles;

}
