package com.ncinga.chatservice.exception;

import com.ncinga.chatservice.utilities.ResponseCode;

public class SinkNotFoundException extends BaseException {
    public SinkNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}
