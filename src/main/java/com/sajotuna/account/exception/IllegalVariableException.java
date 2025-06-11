package com.sajotuna.account.exception;

import org.springframework.http.HttpStatus;

public class IllegalVariableException extends ApiException {
    private static final String MESSAGE = "잘못된 변수 입니다: ";
    public IllegalVariableException(String message) {
        super(HttpStatus.CONFLICT.value(), MESSAGE + message);
    }
}
