package com.sajotuna.account.exception;

import org.springframework.http.HttpStatus;

public class AddressNotFoundException extends ApiException {
    private static final String MESSAGE = "없는 주소입니다.: ";
    public AddressNotFoundException(Long id) {
        super(HttpStatus.CONFLICT.value(), MESSAGE + id);
    }
}
