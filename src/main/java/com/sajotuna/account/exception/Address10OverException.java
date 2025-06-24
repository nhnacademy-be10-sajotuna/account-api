package com.sajotuna.account.exception;

import org.springframework.http.HttpStatus;

public class Address10OverException  extends ApiException {
    private static final String MESSAGE = "주소의 개수가 10개입니다.: ";
    public Address10OverException(Long id) {
        super(HttpStatus.BAD_REQUEST.value(), MESSAGE + id);
    }
}
