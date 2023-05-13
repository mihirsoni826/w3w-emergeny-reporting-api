package com.w3w.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.w3w.exception.*;
import com.w3w.model.Invalid3waErrorMessage;
import com.w3w.model.ThreeWordAddressSuggestions;
import com.w3w.model.ErrorMessage;

import org.jetbrains.annotations.NotNull;
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
@SuppressWarnings("unused")
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    /**
     * Exception handler for custom BadRequestException and HttpClientErrorException.BadRequest
     * @param e Exception thrown
     * @return Invalid3waErrorMessage object with appropriate error message and 400 status code
     * @see Invalid3waErrorMessage
     * @see BadRequestException
     * @see HttpClientErrorException.BadRequest
     */
    @ExceptionHandler({BadRequestException.class, HttpClientErrorException.BadRequest.class})
    @ResponseBody
    public ResponseEntity<Invalid3waErrorMessage> handleBadRequestException(Exception e) {
        Invalid3waErrorMessage error = new Invalid3waErrorMessage();
        error.setMessage(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Exception handler for AutoSuggestException - which is thrown only when user has entered an incorrect/incomplete three word address
     * @param e Exception object thrown
     * @return ThreeWordAddressSuggestions object which holds the error message and correct three word address suggestions
     * @see ThreeWordAddressSuggestions
     * @see AutoSuggestException
     */
    @ExceptionHandler(AutoSuggestException.class)
    public ResponseEntity<ThreeWordAddressSuggestions> handleAutoSuggestException(AutoSuggestException e) {
        ThreeWordAddressSuggestions threeWordAddressSuggestions = new ThreeWordAddressSuggestions();
        threeWordAddressSuggestions.setMessage(e.getMessage());
        threeWordAddressSuggestions.setSuggestions(e.getSuggestions());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(threeWordAddressSuggestions);
    }

    /**
     * Exception handler for W3WApiException - which is thrown when there is an error while calling the W3W API
     * @param e Exception object thrown
     * @return ErrorMessage which holds the appropriate error message and status code 500
     * @see W3WApiException
     * @see ErrorMessage
     */
    @ExceptionHandler(W3WApiException.class)
    public ResponseEntity<ErrorMessage> handleServiceRuntimeException(W3WApiException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(e.getErrorCode());
        errorMessage.setErrorMsg(e.getErrorMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMessage);
    }

    /**
     * Exception handler for JsonParseException - which occurs while parsing JSON request/response payload
     * @param e Exception object thrown
     * @return ErrorMessage object which holds the appropriate error message and status code 400
     * @see JsonParseException
     */
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ErrorMessage> handleJsonParseException(JsonParseException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(HttpStatus.BAD_REQUEST.toString());
        errorMessage.setErrorMsg(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }

    /**
     * Exception handler for RuntimeException
     * @param e Exception object thrown
     * @return ErrorMessage object with appropriate error message and status code 500
     * @see RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleRuntimeException(Exception e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorMessage.setErrorMsg(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMessage);
    }

    /**
     * Overrides the default MethodNotSupported Exception
     * @return ErrorMessage object with appropriate error message and status code 405
     * @see HttpRequestMethodNotSupportedException
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, @NotNull HttpHeaders headers, @NotNull HttpStatusCode status, @NotNull WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setErrorCode(HttpStatus.METHOD_NOT_ALLOWED.toString());
        errorMessage.setErrorMsg(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(errorMessage);
    }




}