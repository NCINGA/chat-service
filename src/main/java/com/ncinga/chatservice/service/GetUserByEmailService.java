package com.ncinga.chatservice.service;
import com.ncinga.chatservice.dto.AzureUserDto;

import java.util.List;

public interface GetUserByEmailService {

    AzureUserDto getUserByEmail(String email);

    Boolean doesUserExist(String email);
}
