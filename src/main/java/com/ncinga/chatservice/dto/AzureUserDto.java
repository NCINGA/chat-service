package com.ncinga.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AzureUserDto {

    private String displayName;
    private String givenName;
    private String surname;
    private String jobTitle;
    private String mail;
    private String mobilePhone;
    private String officeLocation;
    private String userPrincipalName;
    private String id;

}
