package com.sajotuna.account.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {
    private static final String MESSAGE = "존재하지 않는 사용자입니다.: ";
    public UserNotFoundException(String id) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE + id);
    }
}
