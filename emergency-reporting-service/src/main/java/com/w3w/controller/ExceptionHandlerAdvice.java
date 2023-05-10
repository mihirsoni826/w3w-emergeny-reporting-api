package com.w3w.controller;

import com.w3w.exception.*;
import com.w3w.model.Invalid3waErrorMessage;
import com.w3w.model.ThreeWordAddressSuggestions;
import com.w3w.model.ErrorMessage;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(AutoSuggestException.class)
    public ResponseEntity<ThreeWordAddressSuggestions> handleAutoSuggestException(AutoSuggestException e) {
        ThreeWordAddressSuggestions threeWordAddressSuggestions = new ThreeWordAddressSuggestions();
        threeWordAddressSuggestions.setMessage(e.getMessage());
        threeWordAddressSuggestions.setSuggestions(e.getSuggestions());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(threeWordAddressSuggestions);
    }

    @ExceptionHandler(ServiceRuntimeException.class)
    public ResponseEntity<ErrorMessage> handleServiceRuntimeException(Exception e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorMessage.setErrorMessage(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMessage);
    }

}