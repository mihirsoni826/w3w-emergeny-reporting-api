package com.w3w.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.w3w.exception.*;
import com.w3w.model.Invalid3waErrorMessage;
import com.w3w.model.ThreeWordAddressSuggestions;
import com.w3w.model.ErrorMessage;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler({BadRequestException.class, HttpClientErrorException.BadRequest.class})
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

    @ExceptionHandler(W3WApiException.class)
    public ResponseEntity<ErrorMessage> handleServiceRuntimeException(W3WApiException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(e.getErrorCode());
        errorMessage.setErrorMsg(e.getErrorMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMessage);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ErrorMessage> handleJsonParseException(JsonParseException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(HttpStatus.BAD_REQUEST.toString());
        errorMessage.setErrorMsg(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleRuntimeException(Exception e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorMessage.setErrorMsg(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMessage);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(HttpStatus.METHOD_NOT_ALLOWED.toString());
        errorMessage.setErrorMsg(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(errorMessage);
    }




}