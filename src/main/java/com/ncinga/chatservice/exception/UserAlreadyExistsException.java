package com.ncinga.chatservice.exception;

import com.ncinga.chatservice.utilities.ResponseCode;

public class UserAlreadyExistsException extends BaseException {
    public UserAlreadyExistsException(ResponseCode responseCode)
    {
        super(responseCode);
    }
}
