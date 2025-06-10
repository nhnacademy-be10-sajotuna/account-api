package com.sajotuna.account.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyException extends ApiException {

  private static final String MESSAGE = "존재하지 않는 사용자입니다: ";
    public UserAlreadyException(String id) {
        super(HttpStatus.CONFLICT.value(), MESSAGE + id);
    }
}
