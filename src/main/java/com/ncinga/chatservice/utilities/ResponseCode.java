package com.ncinga.chatservice.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    ERROR( "Error" ),
    USER_CREATE_SUCCESS("User create success"),
    USER_CREATE_FAILED("User create failed"),
    USER_ALREADY_EXISTS("User already exists"),
    USER_NOT_FOUND("User not found"),
    USER_UPDATE_SUCCESS("User update success"),
    USER_UPDATE_FAILED("User update failed"),
    USER_DELETE_SUCCESS("User delete success"),
    USER_DELETE_FAILED("User delete failed");
    private String message;
//    private String reason;
    public void setMessage(String message) {
        this.message = message;
    }
//    public void setReason(String reason) {
//        this.reason = reason;
//    }
}
