package com.ncinga.chatservice.exception;

import com.ncinga.chatservice.utilities.ResponseCode;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(ResponseCode responseCode)
    {
        super(responseCode.getMessage());
    }
}
