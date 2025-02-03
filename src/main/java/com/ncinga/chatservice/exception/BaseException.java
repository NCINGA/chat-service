package com.ncinga.chatservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor

public class BaseException extends Exception {
    private String error;
    BaseException(String error) {
        this.error = error;
    }
}
