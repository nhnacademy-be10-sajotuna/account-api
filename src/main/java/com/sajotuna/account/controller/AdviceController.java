package com.sajotuna.account.controller;

import com.sajotuna.account.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class AdviceController {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> handleApiException(ApiException ex) {
        log.error("API Exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }
}
