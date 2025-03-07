package com.ncinga.chatservice.exception;

import com.ncinga.chatservice.utilities.ResponseCode;

public class GeneralException extends BaseException {
    public GeneralException(ResponseCode responseCode)
    {
        super(responseCode);
    }
}
