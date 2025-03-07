package com.ncinga.chatservice.dto;

import com.ncinga.chatservice.document.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MongoUserDto extends BaseDto<MongoUserDto, User> {
    private String username;
    private String password;
    private String email;
    private String role;
}
