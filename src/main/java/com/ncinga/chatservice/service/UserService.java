package com.ncinga.chatservice.service;

import com.ncinga.chatservice.document.User;
import com.ncinga.chatservice.dto.AuthenticateDto;
import com.ncinga.chatservice.dto.SuccessAuthenticateDto;

public interface UserService {
    SuccessAuthenticateDto login(AuthenticateDto authenticateDto) throws Exception;

    User register(User user);

}
