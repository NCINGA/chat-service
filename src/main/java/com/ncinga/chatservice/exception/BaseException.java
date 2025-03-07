package com.ncinga.chatservice.exception;

import com.ncinga.chatservice.utilities.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor

public class BaseException extends Exception {
    private ResponseCode responseCode;
    BaseException(ResponseCode responseCode)
    {
        super(responseCode.getMessage());
        this.responseCode= responseCode;
    }
}
