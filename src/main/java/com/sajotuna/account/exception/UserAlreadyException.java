package com.sajotuna.account.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyException extends ApiException {

  private static final String MESSAGE = "이미 존재하는 사용자 입니다.: ";
    public UserAlreadyException(String id) {
        super(HttpStatus.CONFLICT.value(), MESSAGE + id);
    }
}
