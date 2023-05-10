package com.w3w.controller;

import com.w3w.exception.BadRequestException;
import com.w3w.model.Invalid3waErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ResponseEntity<Invalid3waErrorMessage> handleBadRequestException(Exception e) {
        Invalid3waErrorMessage error = new Invalid3waErrorMessage();
        error.setMessage(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

}